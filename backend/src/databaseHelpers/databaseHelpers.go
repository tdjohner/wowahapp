package databaseHelpers

import (
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
