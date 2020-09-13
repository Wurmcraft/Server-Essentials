package main

import (
	"fmt"
	"github.com/fsnotify/fsnotify"
	"github.com/spf13/viper"
	"time"
)

// Loaded From Config
var address string
var httpsCert string
var httpsKey string
var chunkLoadingUpdate int64
var costPerDay float64
var costPerExtraChunk float64
var rediDBShift int
var chunkLoadingTimeout int64

// Redis Config
var redisAddress string
var redisPass string

// Dynamic based on config
var redisDatabase int
var redisDatabaseUser = redisDatabase
var redisDatabaseRank = redisDatabaseUser + 1
var redisDatabaseAutoRank = redisDatabaseRank + 1
var redisDatabaseTeam = redisDatabaseAutoRank + 1
var redisDatabaseEco = redisDatabaseTeam + 1
var redisDatabaseStatus = redisDatabaseEco + 1
var redisDatabaseTransfer = redisDatabaseStatus + 1
var redisDatabaseDiscord = redisDatabaseTransfer + 1
var redisDatabaseLookup = redisDatabaseDiscord + 1
var redisDatabaseBan = redisDatabaseLookup + 1
var redisDatabaseAuth = redisDatabaseBan + 1
var redisDatabaseChunkLoading = redisDatabaseAuth + 1
var redisCommandStorage = redisDatabaseChunkLoading + 1
var chunkLoadingNotSeenTimeOut int64

func loadAndSetupConfig() {
	viper.SetConfigName("config")
	viper.SetConfigType("toml")
	viper.AddConfigPath(".")
	addDefaults()
	err := viper.ReadInConfig()
	if err != nil {
		_ = viper.SafeWriteConfig()
	}
	copySettings()
	viper.WatchConfig()
	viper.OnConfigChange(func(e fsnotify.Event) {
		fmt.Println("Config file changed!, Reloading Config")
		copySettings()
	})
}

func addDefaults() {
	viper.SetDefault("address", "5050")
	viper.SetDefault("sslCert", "fullchain.pem")
	viper.SetDefault("sslKey", "privkey.pem")
	viper.SetDefault("chunkLoadingUpdateTime", 30)
	viper.SetDefault("costPerDay", 50)
	viper.SetDefault("costPerExtraChunk", 1.2)
	viper.SetDefault("redisDBShift", 0)
	viper.SetDefault("redisAddress", "localhost:6379")
	viper.SetDefault("redisPassword", "")
	viper.SetDefault("chunkLoadingTimeout", 3)
}

func copySettings() {
	address = viper.GetString("Address")
	httpsCert = viper.GetString("sslCert")
	httpsKey = viper.GetString("sslKey")
	chunkLoadingUpdate = viper.GetInt64("chunkLoadingUpdateTime")
	costPerDay = viper.GetFloat64("costPerDay")
	costPerExtraChunk = viper.GetFloat64("costPerExtraChunk")
	redisAddress = viper.GetString("redisAddress")
	redisPass = viper.GetString("redisPass")
	rediDBShift = viper.GetInt("redisDBShift")
	// Update Database ID's
	redisDatabase = rediDBShift
	redisDatabaseUser = rediDBShift
	redisDatabaseRank = redisDatabaseUser + 1
	redisDatabaseAutoRank = redisDatabaseRank + 1
	redisDatabaseTeam = redisDatabaseAutoRank + 1
	redisDatabaseEco = redisDatabaseTeam + 1
	redisDatabaseStatus = redisDatabaseEco + 1
	redisDatabaseTransfer = redisDatabaseStatus + 1
	redisDatabaseDiscord = redisDatabaseTransfer + 1
	redisDatabaseLookup = redisDatabaseDiscord + 1
	redisDatabaseBan = redisDatabaseLookup + 1
	redisDatabaseAuth = redisDatabaseBan + 1
	redisDatabaseChunkLoading = redisDatabaseAuth + 1
	redisCommandStorage = redisDatabaseChunkLoading + 1
	chunkLoadingTimeout = viper.GetInt64("chunkLoadingTimeout")
	chunkLoadingNotSeenTimeOut = chunkLoadingTimeout * (time.Hour.Milliseconds() * 24)
}
