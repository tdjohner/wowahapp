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
	Id int
	Name string
	Quality string
	Class string
	Subclass string
	InventoryType string
	Level int
	PurchasePrice int
	SellPrice int
	IsEquipable bool
	IsStackable bool
}

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

func GetItemByName(name string, db *sql.DB) WoWItem {
	item := WoWItem{}
	q := fmt.Sprintf("SELECT * FROM tblitem WHERE NAME = \"%s\";", name)
	fmt.Println(q)
	/*
	rows, err := db.Query("SELECT * FROM tblitem WHERE NAME = " + name + ";")
	if nil != err {
		fmt.Println("Error getting Item from database: ", err.Error())
	}
	defer rows.Close()
	if rows.Next() {
		rows.Scan(item.id,
			item.name,
			item.quality,
			item.class,
			item.subclass,
			item.inventoryType,
			item.subclass,
			item.level,
			item.purchasePrice,
			item.sellPrice)
	}

	 */

	return item
}