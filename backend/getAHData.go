package main

import (
	"context"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
	"time"

	"github.com/tidwall/gjson"
)

type OauthResponse struct {
	Access_token string
}

type AHItem struct {
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

func main() {
	url := "https://us.api.blizzard.com/data/wow/connected-realm/76/auctions?namespace=dynamic-us&locale=en_US&access_token=" + getAccessToken()
	fmt.Println(url)
	response, err := http.Get(url)
	if nil != err {
		fmt.Println(err)
	}
	defer response.Body.Close()
	auctions := AHItem{}
	body, _ := ioutil.ReadAll(response.Body)
	json.Unmarshal(body, &auctions)
	for k := range auctions.Auctions {
		fmt.Println("AuctionId: ", auctions.Auctions[k].AuctionID)
	}

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
	json, _ := ioutil.ReadFile("web.json")
	str := string(json)
	val := gjson.Get(str, "blizzClient.blizzClientSecret")
	return string(val.String())
}

func getBlizzClient() string {
	json, _ := ioutil.ReadFile("web.json")
	str := string(json)
	val := gjson.Get(str, "blizzClient.blizzClientId")
	return string(val.String())
}
