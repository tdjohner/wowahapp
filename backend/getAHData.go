package main

import (
	"fmt"
	"net/http"

	"github.com/FuzzyStatic/blizzard"
)

func GetAHdata() {

	blizz := blizzard.NewClient("35bde0e980434302b4809be8713efef9", "", blizzard.US, blizzard.EnUS)

	err := blizz.AccessTokenRequest()
	if err != nil {
		fmt.Println(err)
	}

	resp, err := http.Get("https://us.api.blizzard.com/data/wow/connected-realm/101/auctions?namespace=dynamic-us&locale=en_US&access_token=USn2CRJ6mryyfR4eDU6S5zLGCK9McdLui8")
	if err != nil {
		fmt.Println(err)
	}

	fmt.Println(resp.Body)

}
