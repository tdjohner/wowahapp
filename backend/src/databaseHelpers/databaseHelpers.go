package databaseHelpers

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"os"
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

//Get the connection string from our config
func GetConnectionString() string {
	// reading in from web.json from https://stackoverflow.com/questions/16465705/how-to-handle-configuration-in-go
	baseString := "%s:%s@tcp(%s:%s)/%s"

	webFile, _ := os.Open("../web.json")
	defer webFile.Close()
	decoder := json.NewDecoder(webFile)
	webconfig := WebConfig{}
	err := decoder.Decode(&webconfig)
	if err != nil {
		fmt.Println(err)
	}
	return fmt.Sprintf(baseString, webconfig.ConnectionString.User, webconfig.ConnectionString.Pw, webconfig.ConnectionString.Ip, webconfig.ConnectionString.Port, webconfig.ConnectionString.Schema)
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