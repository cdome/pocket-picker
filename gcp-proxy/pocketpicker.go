package pocketpicker

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"time"
)

const apiKey = "84499-681b0af058ae1556838fa1a3"
const requestKeyUrl = "https://getpocket.com/v3/oauth/request"
const authorizeUrl = "https://getpocket.com/v3/oauth/authorize"
const getUrl = "https://getpocket.com/v3/get"
const contentTypeJson = "application/json; charset=UTF8"
const acceptJson = "application/json"

var client = http.Client{
	Timeout: time.Duration(5 * time.Second),
}

func PocketPicker(resp http.ResponseWriter, req *http.Request) {
	var request map[string]interface{}
	err := json.NewDecoder(req.Body).Decode(&request)
	if err != nil {
		log.Fatalln(err)
	}

	resp.Header().Add("Access-Control-Allow-Origin", "*")

	if request["action"] == "key" {
		_, _ = fmt.Fprint(resp, getKey(request["redirectUrl"]))
	} else if request["action"] == "auth" {
		_, _ = fmt.Fprint(resp, authorize(request["code"]))
	} else if request ["action"] == "get" {
		_, _ = fmt.Fprint(resp, get(request["token"]))
	}
}

func getKey(redirectUrl interface{}) interface{} {
	bytesRepresentation, _ := json.Marshal(map[string]interface{}{
		"consumer_key": apiKey,
		"redirect_uri": redirectUrl,
	})
	return postRequest(bytesRepresentation, requestKeyUrl)
}

func authorize(code interface{}) interface{} {
	bytesRepresentation, _ := json.Marshal(map[string]interface{}{
		"consumer_key": apiKey,
		"code":         code,
	})
	return postRequest(bytesRepresentation, authorizeUrl)
}

func get(token interface{}) interface{} {
	bytesRepresentation, _ := json.Marshal(map[string]interface{}{
		"consumer_key": apiKey,
		"access_token": token,
		"state":        "all",
	})
	return postRequest(bytesRepresentation, getUrl)
}

func postRequest(payload []byte, url string) interface{} {
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(payload))
	if err != nil {
		log.Fatalln(err)
	}
	req.Header.Set("Content-Type", contentTypeJson)
	req.Header.Set("X-Accept", acceptJson)
	log.Print("Sending request to ", url)
	resp, err := client.Do(req)
	log.Print("Received response from ", url)
	if err != nil {
		log.Fatalln(err)
	}

	if resp.StatusCode != 200 {
		log.Fatalln("Unable to get content", resp.StatusCode)
	}

	return getString(resp)
}

func getString(resp *http.Response) string {
	bodyBytes, _ := ioutil.ReadAll(resp.Body)
	log.Print("Processed ", len(bodyBytes), " bytes")
	return string(bodyBytes)
}
