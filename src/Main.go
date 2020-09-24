package main

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/go-redis/redis/v8"
	"github.com/gorilla/mux"
	"log"
	"net/http"
	"os"
	"time"
)

const version string = "0.3.1"
const defaultUser string = "admin"
const sslEnabled = false

var redisDBAuth *redis.Client
var ctx = context.Background()

func main() {
	loadAndSetupConfig()
	fmt.Println("Loading Rest-API v" + version + " on " + address)
	router := NewRouter()
	_, err := newClient(0).Ping(ctx).Result()
	if err != nil {
		panic("Unable to connect to RedisDB ")
	}
	fmt.Println("Connected to redis at " + redisAddress + " starting on DataBase " + string(redisDatabase))
	if redisDatabase > 0 {
		fmt.Println("Database is shifted '" + string(redisDatabase))
	}
	redisDBAuth = newClient(redisDatabaseAuth)
	SetupDefaultAuth()
	go checkForExpiredChunkLoading()
	go startupBot()
	go handleMessages()
	if sslEnabled {
		log.Fatal(http.ListenAndServeTLS(":"+address, httpsCert, httpsKey, router))
	} else {
		log.Fatal(http.ListenAndServe(":"+address, router))
	}
}

func NewRouter() *mux.Router {
	router := mux.NewRouter()
	for _, route := range routes {
		if route.RequireAuth {
			router.HandleFunc(route.Pattern, auth(route.Handle)).Methods(route.Method)
		} else {
			router.HandleFunc(route.Pattern, route.Handle).Methods(route.Method)
		}
	}
	router.HandleFunc("/api/chat", MessageSocket)
	return router
}
func newClient(databaseIndex int) *redis.Client {
	return redis.NewClient(&redis.Options{
		Addr:     redisAddress,
		Password: redisPass,
		DB:       redisDatabase + databaseIndex,
	})
}

func SetupDefaultAuth() {
	if redisDBAuth.Exists(ctx, defaultUser).Val() == 0 || len(os.Args) >= 2 && os.Args[2] == "--resetAuth" {
		pass := randomAuthKey(96)
		defaultA := AuthStorage{}
		defaultA.UserId = defaultUser
		defaultA.AuthToken = hashCode(pass)
		defaultA.Permission = []string{permAutoRank, permBan, permChunkLoading, permCommands, permDiscord, permEco, permStatus, permRank, permAuth, permTransfer, permUser, permUUID, permChat, permAuthRenew}
		a, _ := json.Marshal(defaultA)
		redisDBAuth.Set(ctx, defaultA.UserId, a, 0)
		fmt.Println("The default login token is: " + pass + " under user '" + defaultUser + "'")
		fmt.Println("Make sure to save and place the login in a safe place. start this up with --resetAuth to reset the auth login")
	}
}

func calculateCostPerChunks(amount int) float64 {
	var cost = 0.0
	for i := 1; i <= amount; i++ {
		if i == 1 {
			cost += costPerDay
		} else {
			cost += costPerDay * (costPerExtraChunk * float64(i))
		}
	}
	return cost
}

func checkForExpiredChunkLoading() {
	ticker := time.NewTicker(time.Duration(chunkLoadingUpdate) * time.Minute)
	for range ticker.C {
		for entry := range redisChunkLoadingDB.Keys(ctx, "*").Val() {
			var serverChunkLoading ServerChunkData
			json.Unmarshal([]byte(redisChunkLoadingDB.Get(ctx, redisChunkLoadingDB.Keys(ctx, "*").Val()[entry]).Val()), &serverChunkLoading)
			var validChunks []PlayerChunkData
			for data := range serverChunkLoading.PlayerChunkData {
				var d = serverChunkLoading.PlayerChunkData[data]
				if (d.TimeCreated + chunkLoadingNotSeenTimeOut) <= time.Now().Unix() {
					var globalUser GlobalUser
					json.Unmarshal([]byte(redisDBuser.Get(ctx, d.UUID).Val()), &globalUser)
					if (globalUser.LastSeen + chunkLoadingNotSeenTimeOut) > time.Now().Unix() {
						d.TimeCreated = time.Now().Unix()
					} else {
						continue
					}
				}
				var timeToCheck = (d.TimeCreated + (int64(time.Hour.Seconds()) * 24)) <= time.Now().Unix()
				if timeToCheck {
					var cost = calculateCostPerChunks(len(d.Pos))
					if cost <= d.Balance {
						d.Balance = d.Balance - cost
						validChunks = append(validChunks, d)
					}
				}
			}
			serverChunkLoading.PlayerChunkData = validChunks
			output, err := json.Marshal(serverChunkLoading)
			if err != nil {
				return
			}
			redisChunkLoadingDB.Set(ctx, serverChunkLoading.ServerID, output, 0)
		}
	}
}
