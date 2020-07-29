package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis"
	mux "github.com/julienschmidt/httprouter"
	"io/ioutil"
	"math"
	"net/http"
	"strconv"
)

var redisDBStatus *redis.Client

func init() {
	redisDBStatus = newClient(redisDatabaseStatus)
}

func Validate(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	validatedJson, err := json.Marshal(&ValidateResponse{Version: version})
	if err == nil {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write(validatedJson)
	}
}

func GetServerStatus(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	var data []ServerStatus
	for entry := range redisDBStatus.Keys("*").Val() {
		var serverStatus ServerStatus
		json.Unmarshal([]byte(redisDBStatus.Get(redisDBStatus.Keys("*").Val()[entry]).Val()), &serverStatus)
		data = append(data, serverStatus)
	}
	output, err := json.MarshalIndent(data, " ", " ")
	if err != nil || len(output) <= 0 {
		fmt.Fprintln(w, "{}")
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("version", version)
	if string(output) != "null" {
		fmt.Fprintln(w, string(output))
	} else {
		fmt.Fprintln(w, "{}")
	}
}

func PostStatus(w http.ResponseWriter, r *http.Request, _ mux.Params) {
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	var Status ServerStatus
	err = json.Unmarshal(b, &Status)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	output, err := json.Marshal(Status)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	redisDBStatus.Set(Status.ServerID, output, 600000000000)
	w.WriteHeader(http.StatusAccepted)
}

func GetStatus(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	fmt.Fprintln(w, "<html>\n<head>\n <meta http-equiv=\"refresh\" content=\"30\">\n  <link rel=\"stylesheet\" type=\"text/css\" href=\"theme.css\">\n</head>\n<body>\n  <table>\n  \t<tbody>\n ")
	var count = 0
	for entry := range redisDBStatus.Keys("*").Val() {
		var serverStatus ServerStatus
		json.Unmarshal([]byte(redisDBStatus.Get(redisDBStatus.Keys("*").Val()[entry]).Val()), &serverStatus)
		if count%3 == 0 {
			fmt.Fprintln(w, "<tr>\n")
			if count > 2 {
				fmt.Fprintln(w, "</tr>\n")
			}
		}
		count++
		var playerCount = 0
		if len(serverStatus.Players) > 0 {
			playerCount = len(serverStatus.Players)
		}
		var tps = 20
		if int(serverStatus.MS) > 0 {
			tps = 1000 / int(serverStatus.MS)
			if tps > 20 {
				tps = 20
			}
		}
		if serverStatus.Status == "STOPPED" || serverStatus.Status == "ERRORED" {
			fmt.Fprintln(w, "    <td>\n      <div class=\"container\"><img class=\"offline\"><div class=\"centered\">\n           <h1>"+serverStatus.ServerID+"</h1>\n           <font>Status: "+serverStatus.Status+"</font><br>\n           <font>TPS: "+strconv.Itoa(tps)+" ("+strconv.Itoa(int(math.Round(serverStatus.MS)))+" MS)"+"</font><br>\n           <font>Players: "+strconv.Itoa(playerCount)+"</font>\n      </div>\n    </div>\n  </td>")
		} else {
			fmt.Fprintln(w, "    <td>\n      <div class=\"container\"><img class=\"online\"><div class=\"centered\">\n           <h1>"+serverStatus.ServerID+"</h1>\n           <font>Status: "+serverStatus.Status+"</font><br>\n           <font>TPS: "+strconv.Itoa(tps)+" ("+strconv.Itoa(int(math.Round(serverStatus.MS)))+" MS)"+"</font><br>\n           <font>Players: "+strconv.Itoa(playerCount)+"</font>\n      </div>\n    </div>\n  </td>")
		}
	}
	fmt.Fprintln(w, "  \t</tbody>\n  </table>\n</body>\n</html>")
}

func GetCSS(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	data, err := ioutil.ReadFile("theme.css")
	if err != nil {
		fmt.Println("File reading error", err)
		return
	}
	fmt.Fprintln(w, string(data))
	w.Header().Set("Content-Type", "text/css")
}
