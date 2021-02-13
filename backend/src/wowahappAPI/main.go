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

type Tester struct {
	ID  int    `json:"id"`
	Col string `json:"data"`
}

var jsonObject struct {
	Data []Tester `json:"data"`
}

func handleRequest() {

	router := mux.NewRouter().StrictSlash(true)

	router.HandleFunc("/", landingPage)
	router.HandleFunc("/rp/", getRecipe)
	router.HandleFunc("/getitem/{itemName}/", getItem)
	router.HandleFunc("/createuser/", createUser).Methods("POST")
	log.Fatal(http.ListenAndServe(":49155", router))
}

func landingPage(res http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(res, "Hello World: Landing Page")
	fmt.Println("Endpoint: Landing Page")
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

func getRecipe(res http.ResponseWriter, req *http.Request) {

	connectionString := dbh.GetConnectionString()
	db, err := sql.Open("mysql", connectionString)
	if err != nil {
		fmt.Println("Connection to database failed: " + err.Error())
	}
	defer db.Close()

	result, err := db.Query("SELECT * FROM recipe;")
	if err != nil {
		fmt.Println("Error writing to database: " + err.Error())
	} else {
		defer result.Close()

		for result.Next() {
			var t Tester
			result.Scan(&t.ID, &t.Col)
			jsonObject.Data = append(jsonObject.Data, t)
		}

		fmt.Println(result)
		json.NewEncoder(res).Encode(jsonObject)
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


