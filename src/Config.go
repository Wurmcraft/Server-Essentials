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
var discordToken string
var discordAuthTokenSize int
var discordRoleID string
var discordServerID string
var discordLogChannelID string
var discordChannelMap map[string]string

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
var redisDatabaseUUID = redisCommandStorage + 1
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
	viper.SetDefault("discordToken", "bot-token-here")
	viper.SetDefault("discordAuthTokenSize", 12)
	viper.SetDefault("discordRoleID", "")
	viper.SetDefault("discordServerID", "")
	viper.SetDefault("discordLogChannelID", "")
	discordChannelMap = make(map[string]string)
	discordChannelMap["discordChannelID"] = "global"
	discordChannelMap["discordChannelID2"] = "local"
	viper.SetDefault("discordChannelMap", discordChannelMap)
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
	discordToken = viper.GetString("discordToken")
	discordAuthTokenSize = viper.GetInt("discordAuthTokenSize")
	discordRoleID = viper.GetString("discordRoleID")
	discordServerID = viper.GetString("discordServerID")
	discordLogChannelID = viper.GetString("discordLogChannelID")
	discordChannelMap = viper.GetStringMapString("discordChannelMap")
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
	redisDatabaseUUID = redisCommandStorage + 1
	chunkLoadingTimeout = viper.GetInt64("chunkLoadingTimeout")
	chunkLoadingNotSeenTimeOut = chunkLoadingTimeout * (time.Hour.Milliseconds() * 24)
}
