package main

/*
Basic API and muxer implementation from here
https://tutorialedge.net/golang/creating-restful-api-with-golang/

Reading in from web.json from
https://stackoverflow.com/questions/16465705/how-to-handle-configuration-in-go
*/

import (
	dbh "../databaseHelpers"
	"crypto/tls"
	"database/sql"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"golang.org/x/crypto/acme/autocert"
	"time"

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

	//Citation: configuring muxer to work with autocert lib
	// https://stackoverflow.com/questions/50311532/autocert-using-gorilla-mux
	// https://blog.cloudflare.com/exposing-go-on-the-internet/
	manager := &autocert.Manager {
		Prompt: autocert.AcceptTOS,
		Cache: autocert.DirCache("/wowahapp/backend/src/wowahappAPI/secret-dir"),
		HostPolicy: autocert.HostWhitelist("wowahapp.com", "w"),
	}

	router := mux.NewRouter().StrictSlash(true)
	router.HandleFunc("/", landingPage)
	router.HandleFunc("/allrecipes/{realmID}", getAllRecipes)
	router.HandleFunc("/allprofessions/", getProfessions)
	router.HandleFunc("/allexpansions/", getExpansions)
	router.HandleFunc("/itemlisting/{itemName}/{realmID}", getItemListing)
	router.HandleFunc("/getitem/{itemName}/", getItem).Methods("GET")
	router.HandleFunc("/createuser/", createUser).Methods("POST")
	router.HandleFunc("/recipebasecost/{recipeName}/{realmID}", getRecipeBaseCost)

	server := &http.Server {
		Addr:	":https",
		Handler: router,
		ReadTimeout: 45 * time.Second,
		WriteTimeout: 60 * time.Second,
		IdleTimeout: 120 * time.Second,
		TLSConfig: &tls.Config {
			GetCertificate: manager.GetCertificate,
			PreferServerCipherSuites: true,
		},
	}
	log.Fatal(server.ListenAndServeTLS("", ""))
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
	vars := mux.Vars(req)

	fmt.Println("allrecipes endpoint called")

	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	q := "SELECT distinct rcp.id, rcp.name FROM tbl_recipes rcp join tbl_item itm on rcp.name = itm.name join tbl_auctions_current auct on auct.itemID = itm.id where auct.cnctdRealmID = " +vars["realmID"] + ";"
	fmt.Println(q)
	result, err := db.Query(q)
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
func createUser(res http.ResponseWriter, req *http.Request) {

	type NewUser struct {
		Name    string
		Address string
	}

	var newUser NewUser

	// infers body to byte[] stream
	body, _ := ioutil.ReadAll(req.Body)
	fmt.Println(string(body))
	json.Unmarshal(body, &newUser)
	fmt.Printf("user: %s, email: %s", newUser.Name, newUser.Address)
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

 
