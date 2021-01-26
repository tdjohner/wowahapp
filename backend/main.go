package main

/* Basic API and muxer implementation from here
https://tutorialedge.net/golang/creating-restful-api-with-golang/
*/

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"

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
	router.HandleFunc("/recipe", createRecipe).Methods("POST")
	log.Fatal(http.ListenAndServe(":49155", router))
}

func landingPage(res http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(res, "Hello World: Landing Page")
	fmt.Println("Endpoint: Landing Page")
}

func getRecipe(res http.ResponseWriter, req *http.Request) {
	db, err := sql.Open("mysql", "")
	db.Exec("USE test_local_wowahapp;")
	if err != nil {
		fmt.Println("Connection to database failed~~~" + err.Error())
	} else {
		fmt.Println("Connect Success~~~")
	}

	defer db.Close()

	result, err := db.Query("SELECT * FROM recipe;")
	if err != nil {
		fmt.Println("Error writing to database!~~ "+err.Error())
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
func createRecipe(res http.ResponseWriter, req *http.Request) {

	// infers body to byte[] stream
	body, _ := ioutil.ReadAll(req.Body)
	fmt.Fprintf(res, "%+v", string(body))
}
