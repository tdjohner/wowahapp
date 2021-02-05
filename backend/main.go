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
	"os"

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

type WebConfig struct {
	ConnectionString struct {
		User   string `json:"user"`
		Pw     string `json:"pw"`
		Ip     string `json:"ip"`
		Port   string `json:"port"`
		Schema string `json:"schema"`
	}
}

var jsonObject struct {
	Data []Tester `json:"data"`
}

func handleRequest() {

	router := mux.NewRouter().StrictSlash(true)

	router.HandleFunc("/", landingPage)
	router.HandleFunc("/rp/", getRecipe)
	router.HandleFunc("/createuser/", createUser).Methods("POST")
	log.Fatal(http.ListenAndServe(":49155", router))
}

func landingPage(res http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(res, "Hello World: Landing Page")
	fmt.Println("Endpoint: Landing Page")
}

func getRecipe(res http.ResponseWriter, req *http.Request) {

	connectionString := getConnectionString()
	fmt.Println(connectionString)
	db, err := sql.Open("mysql", connectionString)
	db.Exec("USE test_local_wowahapp;")
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

func getConnectionString() string {
	// reading in from web.json from https://stackoverflow.com/questions/16465705/how-to-handle-configuration-in-go
	baseString := "%s:%s@tcp(%s:%s)/%s"

	webFile, _ := os.Open("web.json")
	defer webFile.Close()
	decoder := json.NewDecoder(webFile)
	webconfig := WebConfig{}
	err := decoder.Decode(&webconfig)
	if err != nil {
		fmt.Println(err)
	}
	return fmt.Sprintf(baseString, webconfig.ConnectionString.User, webconfig.ConnectionString.Pw, webconfig.ConnectionString.Ip, webconfig.ConnectionString.Port, webconfig.ConnectionString.Schema)
}
