package main

/*
Basic API and muxer implementation from here
https://tutorialedge.net/golang/creating-restful-api-with-golang/

Reading in from web.json from
https://stackoverflow.com/questions/16465705/how-to-handle-configuration-in-go
*/

import (
	dbh "../databaseHelpers"
	"database/sql"
	"encoding/json"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
	"io/ioutil"
	"log"
	"net/http"
	"strconv"
	"strings"
)

func main() {
	handleRequest()
}

type Recipe struct {
	ID  int    `json:"ID"`
	Name string `json:"Name"`
	URL string `json:"URL"`
}

type RecipeSub struct {
	ID  int    `json:"id"`
	Name string `json:"name"`
	Realm int `json:"realmID"`
	URL string `json:"craftedItemURL"`
}

type RecipeModel struct {
	Name string
	SalePrice int
	Cost int
	Realm int
	URL string
}

type SubbedItem struct {
	RealmID	 	string `realmID`
	RecipeName  string `json:recipeName`
	Username 	string `json:username`
}

func handleRequest() {

	router := mux.NewRouter().StrictSlash(true)
	router.HandleFunc("/", landingPage)
	router.HandleFunc("/allrecipes/{realmID}", getAllRecipes)
	router.HandleFunc("/recipes/{realmID}", getRecipes)
	router.HandleFunc("/allprofessions/", getProfessions)
	router.HandleFunc("/allexpansions/", getExpansions)
	router.HandleFunc("/detailedlisting/{recipeName}/{realmID}", getDetailedListing)
	router.HandleFunc("/getsubbedrecipes/{username}", getSubbedRecipes)
	router.HandleFunc("/itemlisting/{itemName}/{realmID}", getItemListing)
	router.HandleFunc("/getitem/{itemName}/", getItem).Methods("GET")
	router.HandleFunc("/subscriberecipe/", createSubbedItem).Methods("POST")
	router.HandleFunc("/unsubrecipe/", removeSubbedItem).Methods("POST")
	router.HandleFunc("/recipebasecost/{recipeName}/{realmID}", getRecipeBaseCost)
	router.HandleFunc("/allservers/", getServers)

	log.Fatal(http.ListenAndServe(":49155", router))
}

func landingPage(res http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(res, "Hello World: Landing Page")
	fmt.Println("Endpoint: Landing Page")
}

func getDetailedListing(res http.ResponseWriter, req *http.Request) {

	vars := mux.Vars(req)
	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	defer db.Close()
	reagentDetails := dbh.GetDetailedBreakdown(vars["recipeName"], vars["realmID"], db )
	json.NewEncoder(res).Encode(reagentDetails)
}

func getItemListing(res http.ResponseWriter, req *http.Request) {

	vars := mux.Vars(req)
	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	defer db.Close()
	item := dbh.GetAuctionByName(vars["itemName"], vars["realmID"], db)
	json.NewEncoder(res).Encode(item)
}

func getItem(res http.ResponseWriter, req *http.Request) {

	vars := mux.Vars(req)
	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	defer db.Close()
	item := dbh.GetItemByName(vars["itemName"], db)
	json.NewEncoder(res).Encode(item)
}

func getRecipes(res http.ResponseWriter, req *http.Request) {

	vars := mux.Vars(req)
	var recipeModels []RecipeModel
	realmID := vars["realmID"]

	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	recipeList := allRecipesOnConnectedRealm(vars["realmID"], db)

	for i := range recipeList {

		var newRecipe RecipeModel
		listing := dbh.GetAuctionByName(recipeList[i], realmID, db)

		newRecipe.Name = recipeList[i]
		newRecipe.SalePrice = listing.Buyout + listing.UnitPrice
		newRecipe.Cost = dbh.RecipeBaseCost(db, recipeList[i], realmID)
		newRecipe.Realm, _ = strconv.Atoi(realmID)
		recipeModels = append(recipeModels, newRecipe)
	}
	json.NewEncoder(res).Encode(recipeModels)
}

func getAllRecipes(res http.ResponseWriter, req *http.Request) {

	var recipes []Recipe
	vars := mux.Vars(req)

	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	q := "SELECT distinct rcp.id, rcp.name, rcp.craftedItemURL FROM tbl_recipes rcp join tbl_item itm on rcp.name = itm.name join tbl_auctions_current auct on auct.itemID = itm.id where auct.cnctdRealmID = " +vars["realmID"] + ";"
	fmt.Println(q)
	result, err := db.Query(q)
	if err != nil {
		fmt.Println("Error writing to database: " + err.Error())
	} else {
		defer result.Close()

		for result.Next() {
			var r Recipe
			result.Scan(&r.ID, &r.Name, &r.URL)
			recipes = append(recipes, r)
		}
		json.NewEncoder(res).Encode(recipes)
	}
}

func getSubbedRecipes(res http.ResponseWriter, req *http.Request) {

	vars := mux.Vars(req)
	recipeList := getUsersSubs(vars["username"])
	var recipeModels []RecipeModel

	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	if len(recipeList) == 0 {
		json.NewEncoder(res).Encode(make([]string, 0))
	}

	for i := range recipeList {

		var newRecipe RecipeModel
		convertedRealmID := strconv.Itoa(recipeList[i].Realm)
		listing := dbh.GetAuctionByName(recipeList[i].Name, convertedRealmID, db)

		newRecipe.Name = recipeList[i].Name
		newRecipe.SalePrice = listing.Buyout + listing.UnitPrice
		newRecipe.Cost = dbh.RecipeBaseCost(db, recipeList[i].Name, convertedRealmID)
		newRecipe.Realm = recipeList[i].Realm
		newRecipe.URL = recipeList[i].URL
		recipeModels = append(recipeModels, newRecipe)
	}
	json.NewEncoder(res).Encode(recipeModels)
}

func getUsersSubs(username string) []RecipeSub {

	var recipes []RecipeSub

	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	q := 	"select distinct rcp.id, rcp.name, sub.realmID, rcp.craftedItemURL " +
		"FROM tbl_recipes rcp " +
		"join tbl_item itm on rcp.name = itm.name " +
		"join tbl_auctions_current auct on auct.itemID = itm.id " +
		"join tbl_recipe_sub sub on sub.recipeName = rcp.name " +
		"and sub.userName = \"" + username + "\";"

	result, err := db.Query(q)
	if err != nil {
		fmt.Println("Error writing to database: " + err.Error())
	} else {
		defer result.Close()
		for result.Next() {
			var r RecipeSub
			result.Scan(&r.ID, &r.Name, &r.Realm, &r.URL)
			recipes = append(recipes, r)
		}
	}
	return recipes
}

//get all the recipes for the connectedRealm
func allRecipesOnConnectedRealm(realmID string, db *sql.DB) []string {

	var recipeList []string

	// all recipes on realmID
	q := "SELECT distinct rcp.name, rcp.id " +
		"FROM tbl_recipes rcp " +
		"JOIN tbl_auctions_current auct ON auct.itemID = rcp.craftedItemID " +
		"WHERE auct.cnctdRealmID = " + realmID

	result, err := db.Query(q)
	if err != nil {
		fmt.Println("Error reading from database: " + err.Error())
	} else {
		for result.Next() {
			var r Recipe
			result.Scan(&r.Name, &r.ID)
			r.Name = strings.ReplaceAll(r.Name, "\"", "")
			recipeList = append(recipeList, r.Name)
		}
	}
	return recipeList
}

// Create database record example
func createSubbedItem(res http.ResponseWriter, req *http.Request) {

	var newSubbedItem SubbedItem

	// infers body to byte[] stream
	body, _ := ioutil.ReadAll(req.Body)
	json.Unmarshal(body, &newSubbedItem)
	//Insert new SubbedItem to tblSubbedItems

	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	convertedRealmID, _ := strconv.Atoi(newSubbedItem.RealmID)

	_, err = db.Exec("INSERT INTO tbl_recipe_sub(recipeName,username,realmID) VALUES (?,?,?)", newSubbedItem.RecipeName, newSubbedItem.Username, convertedRealmID)

	defer db.Close()
}

func removeSubbedItem(res http.ResponseWriter, req *http.Request) {

	var unsubRecipe SubbedItem

	body, _ := ioutil.ReadAll(req.Body)
	json.Unmarshal(body, &unsubRecipe)
	convertedRealmID, _ := strconv.Atoi(unsubRecipe.RealmID)

	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	defer db.Close()

	_, err = db.Exec("DELETE FROM tbl_recipe_sub WHERE recipeName = ? and username = ? and realmID = ?", unsubRecipe.RecipeName, unsubRecipe.Username, convertedRealmID)

	json.NewEncoder(res).Encode("{\"success\": \"true\"}")
}


func getProfessions(res http.ResponseWriter, req *http.Request) {

	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	defer db.Close()
	professions := dbh.GetAllProfessions(db)
	json.NewEncoder(res).Encode(professions)
}

func getExpansions(res http.ResponseWriter, req *http.Request) {

	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	defer db.Close()
	expacs := dbh.GetAllExpacs(db)
	json.NewEncoder(res).Encode(expacs)
}

func getServers(res http.ResponseWriter, req *http.Request) {
	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	defer db.Close()
	servers := dbh.GetSupportedServers(db)
	json.NewEncoder(res).Encode(servers)
}

func getRecipeBaseCost(res http.ResponseWriter, req *http.Request) {

	vars := mux.Vars(req)

	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	defer db.Close()

	cost := dbh.RecipeBaseCost(db, vars["recipeName"], vars["realmID"])
	convertedCost := float64(cost)/10000
	json.NewEncoder(res).Encode(convertedCost)
}

 
