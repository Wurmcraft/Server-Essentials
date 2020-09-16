package main

// General
type ValidateResponse struct {
	Version string `json:"version"`
}

// User
type GlobalUser struct {
	UUID         string      `json:"uuid"`
	FirstJoin    int64       `json:"firstJoin"`
	LastSeen     int64       `json:"lastSeen"`
	Rank         string      `json:"rank"`
	Perms        []string    `json:"extraPerms"`
	Perks        []string    `json:"perks"`
	Language     string      `json:"language"`
	Muted        bool        `json:"muted"`
	MuteTime     float64     `json:"muteExpire"`
	Wallet       Wallet      `json:"wallet"`
	NetworkTime  NetworkTime `json:"playtime"`
	DiscordID    string      `json:"discordID"`
	RewardPoints int         `json:"rewardPoints"`
}

type Wallet struct {
	Currency []Currency `json:"currency"`
}

type Currency struct {
	Name   string  `json:"name"`
	Amount float64 `json:"amount"`
}

type NetworkTime struct {
	ServerTime []ServerTime `json:"serverTime"`
}

type ServerTime struct {
	ServerID string `json:"serverID"`
	Amount   int64  `json:"time"`
}

type AllPlayers struct {
	Players []UserSimple `json:"players"`
}

type UserSimple struct {
	UUID    string `json:"uuid"`
	Rank    string `json:"rank"`
	Discord string `json:"discordID"`
	Wallet  Wallet `json:"wallet"`
}

type UserLookup struct {
	UUID string `json:"uuid"`
	Name string `json:"name"`
}

// Rank
type Rank struct {
	Name        string   `json:"name"`
	Prefix      string   `json:"prefix"`
	Suffix      string   `json:"suffix"`
	Inheritance []string `json:"inheritance"`
	Permission  []string `json:"permission"`
}

// AutoRank
type AutoRank struct {
	PlayTime int32  `json:"playTime"`
	Balance  int64  `json:"balance"`
	Exp      int32  `json:"exp"`
	Rank     string `json:"rank"`
	NextRank string `json:"nextRank"`
}

// Eco / Currency
type CurrencyConvert struct {
	Name  string  `json:"name"`
	Worth float64 `json:"worth"`
}

// Server Data
type ServerStatus struct {
	ServerID       string   `json:"serverID"`
	Status         string   `json:"status"`
	Players        []string `json:"players"`
	ModpackVersion string   `json:"modpackVersion"`
	MS             float64  `json:"ms"`
	LastUpdate     int64    `json:"lastUpdate"`
}

type TransferBin struct {
	UUID string    `json:"uuid"`
	Bins []ItemBin `json:"transfers"`
}

type ItemBin struct {
	TransferID string   `json:"transferID"`
	Storage    []string `json:"items"`
}

type Token struct {
	DiscordID string `json:"id"`
	Token     string `json:"token"`
}

// Ban
type Ban struct {
	Name   string `json:"name"`
	UUID   string `json:"uuid"`
	IP     string `json:"ip"`
	Reason string `json:"reason"`
}

type Auth struct {
	Username string `json:"user"`
	Password string `json:"password"`
}

type ServerChunkData struct {
	ServerID        string            `json:"serverID"`
	PlayerChunkData []PlayerChunkData `json:"playerChunks"`
}

type PlayerChunkData struct {
	UUID        string     `json:"uuid"`
	Pos         []ChunkPos `json"pos"`
	Balance     float64    `json:"storedCurrency"`
	TimeCreated int64      `json:"setupTime"`
}

type ChunkPos struct {
	X int `json:"x"`
	Z int `json:"z"`
}

type CommandRequest struct {
	ServerID       string `json:"serverID"`
	Command        string `json:"command"`
	RequiredPlayer string `json:"requiredPlayer"`
}

type CommandQueue struct {
	Commands []CommandRequest `json:"commands"`
}
