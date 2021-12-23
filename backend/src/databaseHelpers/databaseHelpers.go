package databaseHelpers

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"os"
	"sort"
)

type WebConfig struct {
	ConnectionString struct {
		User   string `json:"user"`
		Pw     string `json:"pw"`
		Ip     string `json:"ip"`
		Port   string `json:"port"`
		Schema string `json:"schema"`
	}
}

type WoWItem struct {
	Id int `db:id`
	Name string `db:name`
	Quality string `db:quality`
	Class string `db:class`
	Subclass string `db:subclass`
	InventoryType string `db:inventorytype`
	Level int `db:level`
	PurchasePrice int `db:purchaseprice`
	SellPrice int `db:sellprice`
	IsEquipable bool `db:isequipable`
	IsStackable bool `db:isstackable`
}

type AuctionSlice struct {
	Name string `db:name`
	UnitPrice int `db:unitPrice`
	Buyout int `db:buyout`
}

type ReagentItem struct {
	Name string `db:name`
	Quantity int `db:quantity`
	Cost int `db:cost`
	Available int `db:available`
}

type ReagentListing struct {
	quantity int
	cost int
}

type GameServer struct {
	CnctdRealmID int `db:cnctdRealmID`
	RealmName	string `db:realmName`
}

//Get the connection string from our config
func GetConnectionString() string {
	// reading in from web.json from https://stackoverflow.com/questions/16465705/how-to-handle-configuration-in-go
	baseString := "%s:%s@tcp(%s:%s)/%s"

	webFile, _ := os.Open("C:\\Users\\tdjoh\\Desktop\\wowahapp\\backend\\src\\web.json")
	defer webFile.Close()
	fmt.Print(webFile)
	decoder := json.NewDecoder(webFile)
	webconfig := WebConfig{}
	err := decoder.Decode(&webconfig)
	if err != nil {
		fmt.Println(err)
	}
	return fmt.Sprintf(baseString, webconfig.ConnectionString.User, webconfig.ConnectionString.Pw, webconfig.ConnectionString.Ip, webconfig.ConnectionString.Port, webconfig.ConnectionString.Schema)
}

func GetDetailedBreakdown(name string, realmID string, db *sql.DB) []ReagentItem{
	var reagents = []ReagentItem{}

	q := fmt.Sprintf("select rgt.name, rgt.quantity, auct.unitPrice + auct.buyout as cost, auct.quantity as available " +
		"from tbl_recipes rp " +
	"join tbl_reagents rgt on rgt.recipeID = rp.id " +
	"join tbl_auctions_current auct on auct.itemID = rgt.reagentItemID " +
	"where rp.name = \"%s\" and cnctdRealmID = %s;",name, realmID)

	rows, err := db.Query(q)
	if nil != err {
		fmt.Println("Error recipe base cost from database: ", err.Error())
	}
	defer rows.Close()

	for rows.Next() {
		var r ReagentItem
		_ = rows.Scan(&r.Name, &r.Quantity, &r.Cost, &r.Available)
		reagents = append(reagents, r)
	}
	return reagents
}


func GetAuctionByName(name string, realmID string, db *sql.DB) AuctionSlice {
	var auct AuctionSlice

	q := fmt.Sprintf(  "SELECT name, unitPrice, buyout FROM tbl_auctions_current auct JOIN tbl_item itm on itm.id = auct.itemID WHERE name = \"%s\" and cnctdRealmID = \"%s\" ORDER BY (unitPrice + buyout);", name, realmID )

	rows, err := db.Query(q)
	if nil != err {
		fmt.Println("Error getting Auction Slice from database: ", err.Error())
		fmt.Println("Query: ", q)
	}
	defer rows.Close()
	if rows.Next() {
		err = rows.Scan(&auct.Name, &auct.UnitPrice, &auct.Buyout)
		if nil != err {
			fmt.Println("Error marshalling DB object: ", err.Error())
		}
	}
	return auct
}

//Get item from our database by it's name
func GetItemByName(name string, db *sql.DB) WoWItem {
	var item WoWItem
	q := fmt.Sprintf("SELECT * FROM tbl_item WHERE name = \"%s\";", name)
	rows, err := db.Query(q)
	if nil != err {
		fmt.Println("Error getting Item from database: ", err.Error())
	}
	defer rows.Close()
	if rows.Next() {
		err = rows.Scan(&item.Id,
			&item.Name,
			&item.Quality,
			&item.Class,
			&item.Subclass,
			&item.InventoryType,
			&item.Level,
			&item.PurchasePrice,
			&item.SellPrice,
			&item.IsEquipable,
			&item.IsStackable)
		if nil != err {
			fmt.Println("Error marshalling DB object: ", err.Error())
		}
	}
	return item
}

//Get Professions from our database
func GetAllProfessions(db *sql.DB) []string {
	professions := []string{}
	q := "SELECT name FROM lu_professions"
	rows, err := db.Query(q)
	if nil != err {
		fmt.Println("Error retrieving professions from database: ", err.Error())
	}
	defer rows.Close()
	for rows.Next() {
		var p string
		err = rows.Scan(&p)
		professions = append(professions, p)
	}
	return professions
}

//Get Expansions from our database
func GetAllExpacs(db *sql.DB) []string {
	expacs := []string{}
	q := "SELECT name FROM lu_expansions"
	rows, err := db.Query(q)
	if nil != err {
		fmt.Println("Error retrieving professions from database: ", err.Error())
	}
	defer rows.Close()
	for rows.Next() {
		var p string
		err = rows.Scan(&p)
		expacs = append(expacs, p)
	}
	return expacs
}

func GetSupportedServers(db *sql.DB) []GameServer {
	servers := []GameServer{}
	q := "SELECT cnctdRealmID, realmName from tbl_connected_realm"
	rows, err := db.Query(q)
	if nil != err {
		fmt.Println("Error retrieving supported servers from the database: ", err.Error())
	}
	for rows.Next() {
		var gs GameServer
		err = rows.Scan(&gs.CnctdRealmID, &gs.RealmName)
		servers = append(servers, gs)
	}
	return servers
}

//returns -1 if there are not enough of a type of reagent to craft the recipe
func RecipeBaseCost(db *sql.DB, name string, realm string) int {

	reagents := make(map[string][]ReagentItem)
	cost := 0

	q := fmt.Sprintf("select rgt.name, rgt.quantity, auct.unitPrice + auct.buyout as cost, auct.quantity as available " +
		"from tbl_recipes rp " +
		"join tbl_reagents rgt on rgt.recipeID = rp.id " +
		"join tbl_auctions_current auct on auct.itemID = rgt.reagentItemID " +
		"where rp.name = \"%s\" and cnctdRealmID = %s;",name, realm)

	rows, err := db.Query(q)
	if nil != err {
		fmt.Println("Error recipe base cost from database: ", err.Error())
	}
	defer rows.Close()

	for rows.Next() {
		var reagent ReagentItem
		_ = rows.Scan(&reagent.Name, &reagent.Quantity, &reagent.Cost, &reagent.Available)
		println(reagent.Name, reagent.Cost, reagent.Quantity, reagent.Available)
		reagents[reagent.Name] = append(reagents[reagent.Name], reagent)
	}
	if len(reagents) == 0 {
		return -1 //no reagents for the recipe posted
	}
	//for each reagent
	for key := range reagents {
		//sort lowest cost
		sort.Slice(reagents[key][:], func(i, j int) bool {
			return reagents[key][i].Cost < reagents[key][j].Cost
		}) //citation for sorting slices: https://stackoverflow.com/questions/28999735/what-is-the-shortest-way-to-simply-sort-an-array-of-structs-by-arbitrary-field

		//get the number of reagents required, at the cheapest listing
		required := reagents[key][0].Quantity
		 r, x  := 0, 0
		for r < required && x < len(reagents[key])  {
			if reagents[key][x].Available > 0 {
				cost = cost + reagents[key][x].Cost
				reagents[key][x].Available = reagents[key][x].Available - 1
				r = r + 1 // reagent "consumed", next reagent in listing.
			} else {
				x = x + 1 // auction listing exhausted, next auction listing.
				continue
			}
		}
		if r < required {
			return -10000 //bail if we have insufficient reagent values
		}
	}
	return cost
}


