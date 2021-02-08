package main

import (
	"context"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"strconv"
	"strings"
	"time"


	"github.com/tidwall/gjson"
)

type OauthResponse struct {
	Access_token string
}

type AuctionLedger struct {
	Auctions []struct {
		AuctionID int `json:"id"`
		Item      struct {
			ItemID     int `json:"id"`
			BonusLists []int
			Modifiers  []struct {
				Type  int `json:"type"`
				Value int `json:"value"`
			}
		}
		Quantity  int    `json:"quantity"`
		UnitPrice int    `json:"unit_price"`
		TimeLeft  string `json:"time_left"`
	}
}

type Items struct {
	ID      int    `json:"id"`
	Name    string `json:"name"`
	Quality struct {
		Type string `json:"type"`
		Name string `json:"name"`
	}
	Level      int `json:"level"`
	Item_Class struct {
		Name string `json:"name"`
		Id   int    `json:"id"`
	}
	Item_subclass struct {
		Name string `json:"name"`
		Id   int    `json:"id"`
	}
	Purchase_Price int  `json:"purchase_price"`
	Sell_Price     int  `json:"sell_price"`
	Is_Equippable  bool `json:"is_equippable"`
	Is_Stackable   bool `json:"is_stackable"`
	Inventory_Type struct {
		Type string `json:"type"`
		Name string `json:"name"`
	}
}

func main() {
	ledger := getAuctions(76, getAccessToken())
	for _, a := range ledger.Auctions {
		fmt.Println(a.AuctionID)
	}

}

func getAuctions(realmID int, accessToken string) AuctionLedger {
	url := "https://us.api.blizzard.com/data/wow/connected-realm/%d/auctions?namespace=dynamic-us&locale=en_US&access_token="
	url = fmt.Sprintf( url + accessToken, realmID)
		response, err := http.Get(url)
	if nil != err {
		fmt.Println(err)
	}
	defer response.Body.Close()
	ledger := AuctionLedger{}
	body, _ := ioutil.ReadAll(response.Body)
	json.Unmarshal(body, &ledger)
	return ledger
}


func getAccessToken() string {

	ctx, cancel := context.WithDeadline(context.Background(), time.Now().Add(15*time.Second))
	defer cancel()
	request, err := http.NewRequestWithContext(ctx, "POST", "https://us.battle.net/oauth/token", strings.NewReader("grant_type=client_credentials"))
	if nil != err {
		fmt.Println(err)
	}
	request.Header.Set("Content-Type", "application/x-www-form-urlencoded")
	request.SetBasicAuth(getBlizzClient(), getBlizzSecret())
	client := http.Client{}
	response, err := client.Do(request)
	if nil != err {
		fmt.Println(err)
	} else if response.StatusCode != 200 {
		fmt.Println("Http error fetching authentication token: " + fmt.Sprint(response.StatusCode))
	}
	defer response.Body.Close()
	body, _ := ioutil.ReadAll(response.Body)
	r := OauthResponse{}
	err = json.Unmarshal(body, &r)

	return r.Access_token
}

func getBlizzSecret() string {
	json, _ := ioutil.ReadFile("../web.json")
	str := string(json)
	val := gjson.Get(str, "blizzClient.blizzClientSecret")
	return string(val.String())
}

func getBlizzClient() string {
	json, _ := ioutil.ReadFile("../web.json")
	str := string(json)
	val := gjson.Get(str, "blizzClient.blizzClientId")
	return string(val.String())
}

func PullItem(id int) Items {
	var Item Items

	url := "https://us.api.blizzard.com/data/wow/item/" + strconv.Itoa(id) + "?namespace=static-us&locale=en_US&access_token=" + getAccessToken()

	resp, err := http.Get(url)
	if err != nil {
		log.Fatal(err)
	}
	body, _ := ioutil.ReadAll(resp.Body)
	err = json.Unmarshal(body, &Item)

	return Item

}
