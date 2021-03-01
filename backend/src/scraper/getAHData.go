package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"strconv"
	"strings"
	"time"

	dbh "../databaseHelpers"
	_ "github.com/go-sql-driver/mysql"
	"github.com/tidwall/gjson"
)


var tableName = "tbl_auctions_current"

var newAuctionTableQuery = "CREATE TABLE tbl_auctions_current (" +
	"id INT unsigned auto_increment primary key, " +
	"cnctdRealmID INT, " +
	"auctionID INT, " +
	"itemID INT, " +
	"quantity INT, " +
	"unitPrice BIGINT, " +
	"bid BIGINT, " +
	"buyout BIGINT, " +
	"timeLeft VARCHAR(16))"

func main() {
	//scrapeRecipes()
	supportedRealms := getSupportedRealms()
	connectionString := dbh.GetConnectionString()
	accessToken := getAccessToken()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	//First we rename the old table. This is currently our only method of archiving historical price data
	archiveTableName := fmt.Sprintf("aucts_date%s",time.Now().Local().Format("2006_01_02_15_04_05"))
	renameQuery := fmt.Sprintf("RENAME TABLE tbl_auctions_current TO %s", archiveTableName)
	conn, err := db.Query(renameQuery)
	if nil != err {
		fmt.Println("Error creating Archive Auction Table: ", err)
	}
	conn.Close()
	conn, err = db.Query(newAuctionTableQuery)
	if nil != err {
		fmt.Println("Error creating a new Auction Table: ", err)
	}
	conn.Close()

	for _, realm := range supportedRealms.SupportedRealms {
		//now we put all the realms auctions in the new table
		auctions := PullAuctions(realm, getAccessToken())
		for _, auction := range auctions.Auctions {
			PushAuction(auction, tableName, realm, db)
		}

		// get all the ItemIDs from the auctions we just inserted
		var incomingID int
		var incomingIDs []int
		conn, err = db.Query(fmt.Sprintf("SELECT DISTINCT itemID from %s order by itemID", tableName))
		if nil != err {
			fmt.Println("Error populating incomingIDs: ", err)
		}
		for conn.Next() {
			conn.Scan(&incomingID)
			incomingIDs = append(incomingIDs, incomingID)
		}
		conn.Close()

		// get all existing ItemIDs from the Items table
		var existingID int
		var existingIDs []int
		conn, err = db.Query(fmt.Sprintf("SELECT DISTINCT id from tbl_item order by id"))
		if nil != err {
			fmt.Println("Error querying existingIDs: ", err)
		}
		for conn.Next() {
			conn.Scan(&existingID)
			existingIDs = append(existingIDs, existingID)
		}
		conn.Close()

		// Iterate over all Items and add any we don't have
		var matchFlag bool
		for _, k := range incomingIDs {
			matchFlag = false
			for _, j := range existingIDs {
				if k == j {
					matchFlag = true
					break // match found, not to be added
				}
			}
			if !matchFlag {
				// no match in database, pull Item from blizzard API and store it
				fmt.Println("Adding new item: ", k)
				newItem, cheapErr := PullItem(k, accessToken)
				if 404 == cheapErr {
					fmt.Println("Failed to retrieve item from database: ", k)
					continue
				}
				PushItem(newItem, db)
				time.Sleep(500 * time.Millisecond)
			}
		}
	}

}

type Realms struct {
	SupportedRealms []int
}

type OauthResponse struct {
	Access_token string
}

type AuctionLedger struct {
	Auctions []Auction
}

type Auction struct {
	AuctionID int `json:"id"`
	Item      struct {
		ItemID int `json:"id"`
	}
	Quantity  int    `json:"quantity"`
	UnitPrice int    `json:"unit_price"`
	Bid       int    `json:"bid"`
	Buyout    int    `json:"buyout"`
	TimeLeft  string `json:"time_left"`
}

type Item struct {
	ID      int    `json:"id"`
	Name    string `json:"name"`
	Quality struct {
		Type string `json:"type"`
		Name string `json:"name"`
	}
	Level      int `json:"level"`
	Item_Class struct {
		Name string `json:"name"`
		Id   int    `json:"id"`
	}
	Item_Subclass struct {
		Name string `json:"name"`
		Id   int    `json:"id"`
	}
	PurchasePrice  int  `json:"purchase_price"`
	SellPrice      int  `json:"sell_price"`
	IsEquippable   bool `json:"is_equippable"`
	IsStackable    bool `json:"is_stackable"`
	Inventory_Type struct {
		Type string `json:"type"`
		Name string `json:"name"`
	}
}

type Recipes struct {
	ID       int    `json:"id"`
	Name     string `json:"name"`
	Reagents []struct {
		Reagent struct {
			Name string `json:"name"`
			ID   int    `json:"id"`
		}
		Quantity int `json:"quantity"`
	}
	CraftedQuantity struct {
		Minimum int `json:"minimum"`
		Maximum int `json:"maximum"`
	}
}

func getSupportedRealms() Realms {
	supportedRealms := Realms{}
	body, _ := ioutil.ReadFile("conf.json")
	json.Unmarshal(body, &supportedRealms)
	return supportedRealms
}

func PullAuctions(realmID int, accessToken string) AuctionLedger {
	url := "https://us.api.blizzard.com/data/wow/connected-realm/%d/auctions?namespace=dynamic-us&locale=en_US&access_token="
	url = fmt.Sprintf(url+accessToken, realmID)
	response, err := http.Get(url)
	if nil != err {
		fmt.Println(err)
	}
	defer response.Body.Close()
	ledger := AuctionLedger{}
	body, _ := ioutil.ReadAll(response.Body)
	json.Unmarshal(body, &ledger)
	return ledger
}

func getAccessToken() string {

	ctx, cancel := context.WithDeadline(context.Background(), time.Now().Add(15*time.Second))
	defer cancel()
	request, err := http.NewRequestWithContext(ctx, "POST", "https://us.battle.net/oauth/token", strings.NewReader("grant_type=client_credentials"))
	if nil != err {
		fmt.Println(err)
	}
	request.Header.Set("Content-Type", "application/x-www-form-urlencoded")
	request.SetBasicAuth(getBlizzClient(), getBlizzSecret())
	client := http.Client{}
	response, err := client.Do(request)
	if nil != err {
		fmt.Println(err)
	} else if response.StatusCode != 200 {
		fmt.Println("Http error fetching authentication token: " + fmt.Sprint(response.StatusCode))
	}
	defer response.Body.Close()
	body, _ := ioutil.ReadAll(response.Body)
	r := OauthResponse{}
	err = json.Unmarshal(body, &r)

	return r.Access_token
}

func getBlizzSecret() string {
	json, _ := ioutil.ReadFile("../web.json")
	str := string(json)
	val := gjson.Get(str, "blizzClient.blizzClientSecret")
	return string(val.String())
}

func getBlizzClient() string {
	json, _ := ioutil.ReadFile("../web.json")
	str := string(json)
	val := gjson.Get(str, "blizzClient.blizzClientId")
	return string(val.String())
}

func PullItem(id int, accessToken string) (Item, int) {
	var Item Item
	var error int
	url := "https://us.api.blizzard.com/data/wow/item/" + strconv.Itoa(id) + "?namespace=static-us&locale=en_US&access_token=" + accessToken

	resp, err := http.Get(url)
	if err != nil {
		log.Fatal(err)
	} else if 404 == resp.StatusCode {
		error = 404
	}
	body, _ := ioutil.ReadAll(resp.Body)
	err = json.Unmarshal(body, &Item)

	return Item, error
}

func PullRecipe(id int, accessToken string) (Recipes, int) {
	var Recipe Recipes
	var error int
	url := "https://us.api.blizzard.com/data/wow/recipe/" + strconv.Itoa(id) + "?namespace=static-us&locale=en_US&access_token=" + accessToken

	resp, err := http.Get(url)
	if err != nil {
		log.Fatal(err)
	} else if 404 == resp.StatusCode {
		error = 404
	}
	body, _ := ioutil.ReadAll(resp.Body)
	err = json.Unmarshal(body, &Recipe)

	return Recipe, error
}

//place Item row in our database
func PushItem(item Item, db *sql.DB) {

	query := "INSERT INTO tbl_item (id, name, quality, class, subclass, inventoryType, level, purchasePrice, sellPrice, isEquipable, isStackable) " +
		"VALUES (%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, %d, %t, %t)"

	query = fmt.Sprintf(query,
		item.ID, // hideous but instances of quotes crash our queries
		strings.Replace(strings.Replace(item.Name, "'", "", -1), "\"", "", -1),
		item.Quality.Name,
		item.Item_Class.Name,
		item.Item_Subclass.Name,
		item.Inventory_Type.Name,
		item.Level,
		item.PurchasePrice,
		item.SellPrice,
		item.IsEquippable,
		item.IsStackable)
	q, err := db.Query(query)
	defer q.Close()
	if nil != err {
		fmt.Println("Error Inserting Item: ", err.Error())
		fmt.Println(query)
	}
}


func PushAuction(auction Auction, tableName string, realm int,  db *sql.DB) {

	query := fmt.Sprintf("INSERT INTO %s (unitPrice, bid, buyout, auctionID, itemID, quantity, cnctdRealmID, timeLeft) " +
		"VALUES (%d, %d, %d, %d, %d, %d, %d, \"%s\");",
		tableName,
		auction.UnitPrice,
		auction.Bid,
		auction.Buyout,
		auction.AuctionID,
		auction.Item.ItemID,
		auction.Quantity,
		realm,
		auction.TimeLeft)

	rows, err := db.Query(query)
	if nil != err {
		fmt.Println("Error inserting Auction row: ", err)
	}
	rows.Close()
}

func checkItemExists(id int) bool {
	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	rows, err := db.Query("SELECT * FROM tbl_item WHERE id = " + string(id))
	defer rows.Close()
	if rows.Next() {
		return true
	} else {
		return false
	}
}

func scrapeRecipes() {
	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	//UNCOMMENT THIS IF TABLES NOT CREATE ALREADY.
	//createTables(db)
	//Possibly does not need to be as high as 100k. No recipes exist lower than 1800.
	for i := 1800; i < 100000; i++ {
		var Recipe Recipes
		Recipe, myerr := PullRecipe(i, getAccessToken())
		//Check if 404, no recipe for i
		if myerr == 404 {
			fmt.Println("No recipe available for ID ", i)
			time.Sleep(250 * time.Millisecond)
			continue
		}
		fmt.Printf("%+v\n", Recipe)
		//Insert Recipe into Recipes table if non 404.
		query, err := db.Query("INSERT INTO tbl_recipes(ID,Name) VALUES (?,?)", Recipe.ID, Recipe.Name)
		query.Close()
		if nil != err {
			fmt.Println("Error Inserting Recipe: ", err.Error())
			fmt.Println(query)
		}
		//Now insert into reagent table.
		//Can add reagentitemid if needed.
		for i := 0; i < len(Recipe.Reagents); i++ {
			reagentQuery, err := db.Query("INSERT INTO tbl_reagents(recipeID,name,category,quantity) VALUES (?,?,?,?)",
				Recipe.ID, Recipe.Reagents[i].Reagent.Name, "none", Recipe.Reagents[i].Quantity)

			reagentQuery.Close()
			if nil != err {

				fmt.Println("Error Inserting Reagent: ", err.Error())
				fmt.Println(reagentQuery)
			}
		}
		fmt.Println("Successfully inserted \n", Recipe.Name)
		time.Sleep(500 * time.Millisecond)
	}
}

//Create our recipes and reagents table
func createTables(db *sql.DB) {

	var recipeTableBaseQuery = "CREATE TABLE tbl_recipes (" +
		"id INT  NOT NULL AUTO_INCREMENT primary key, " +
		"name VARCHAR(45))"

	var reagentsTableBaseQuery = "CREATE TABLE tbl_reagents (" +
		"category varchar(45), " +
		"name varchar(45), " +
		"quantity INT, " +
		"reagentItemID INT, " +
		"recipeID INT)"
	//Create the recipe table
	conn, err := db.Query(recipeTableBaseQuery)
	if nil != err {
		fmt.Println("Error creating a new recipe Table: ", err)
	}
	conn.Close()

	//Create the reagents table
	newcon, err := db.Query(reagentsTableBaseQuery)
	if nil != err {
		fmt.Println("Error creating a new reagents Table: ", err)
	}
	newcon.Close()
}
