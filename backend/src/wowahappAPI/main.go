package main

/*
Basic API and muxer implementation from here
https://tutorialedge.net/golang/creating-restful-api-with-golang/

Reading in from web.json from
https://stackoverflow.com/questions/16465705/how-to-handle-configuration-in-go
*/

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	dbh "../databaseHelpers"

	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
)

func main() {
	handleRequest()
}

type Recipe struct {
	ID  int    `json:"id"`
	Name string `json:"name"`
}

func handleRequest() {

	router := mux.NewRouter().StrictSlash(true)

	router.HandleFunc("/", landingPage)
	router.HandleFunc("/allrecipes/", getAllRecipes)
	router.HandleFunc("/allprofessions/", getProfessions)
	router.HandleFunc("/allexpansions/", getExpansions)
	router.HandleFunc("/itemlisting/{itemName}/{realmID}", getItemListing)
	router.HandleFunc("/getitem/{itemName}/", getItem).Methods("GET")
	router.HandleFunc("/createuser/", createSubbedItem).Methods("POST")
	log.Fatal(http.ListenAndServe(":49155", router))
}

func landingPage(res http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(res, "Hello World: Landing Page")
	fmt.Println("Endpoint: Landing Page")
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

func getAllRecipes(res http.ResponseWriter, req *http.Request) {

	var recipes []Recipe

	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	result, err := db.Query("SELECT * FROM tbl_recipes;")
	if err != nil {
		fmt.Println("Error writing to database: " + err.Error())
	} else {
		defer result.Close()

		for result.Next() {
			var r Recipe
			result.Scan(&r.ID, &r.Name)
			recipes = append(recipes, r)
		}

		json.NewEncoder(res).Encode(recipes)
	}
}

// Create database record example
func createSubbedItem(res http.ResponseWriter, req *http.Request) {

	type SubbedItem struct {
		itemID    int
		userId 	  int
	}

	var newSubbedItem SubbedItem

	// infers body to byte[] stream
	body, _ := ioutil.ReadAll(req.Body)
	fmt.Println(string(body))
	json.Unmarshal(body, &newSubbedItem)
	fmt.Printf("itemID: %s, userID: %s", newSubbedItem.itemID, newSubbedItem.userId)
	//Insert new SubbedItem to tblSubbedItems (Was not able to test this, inserting may not work)

	db, err := sql.Open("mysql", dbh.GetConnectionString())
	if nil != err {
		fmt.Println("Error connecting to database: ", err.Error())
	}
	_, err = db.Exec("INSERT INTO tblSubbedItems(itemId,userId) VALUES (?,?)", newSubbedItem.userId, newSubbedItem.itemID)
	//sqlInsert := "INSERT INTO tblSubbedItems(itemId, userId) VALUES ($1, $2)
	defer db.Close()

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

