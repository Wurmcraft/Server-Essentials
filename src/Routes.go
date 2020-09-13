package main

import (
	"encoding/json"
	"fmt"
	mux "github.com/julienschmidt/httprouter"
	"golang.org/x/crypto/bcrypt"
	"net/http"
)

type Route struct {
	Name        string
	Method      string
	Pattern     string
	RequireAuth bool
	Handle      mux.Handle
}

type Routes []Route

var routes = Routes{
	// User
	Route{
		"Index",
		"GET",
		"/",
		false,
		Index,
	},
	Route{
		"Validate",
		"GET",
		"/api/validate",
		true,
		Validate,
	},
	Route{
		"GetUsers",
		"GET",
		"/api/user/:uuid",
		false,
		GetGlobalUser,
	},
	Route{
		"AddUser",
		"POST",
		"/api/user/add",
		true,
		SetGlobalUser,
	},
	Route{
		"OverrideUser",
		"PUT",
		"/api/user/:uuid/override",
		true,
		SetGlobalUser,
	},
	Route{
		"AllUsers",
		"GET",
		"/api/user",
		false,
		GetAllUsers,
	},
	// Rank
	Route{
		"GetRank",
		"GET",
		"/api/rank/:name",
		false,
		GetRank,
	},
	Route{
		"SetRank",
		"POST",
		"/api/rank/add",
		true,
		SetRank,
	},
	Route{
		"OverrideRank",
		"PUT",
		"/api/rank/:name/override",
		true,
		SetRank,
	},
	Route{
		"AllRanks",
		"GET",
		"/api/rank",
		false,
		GetAllRanks,
	},
	Route{
		"DelRank",
		"DELETE",
		"/api/rank/:name/del",
		true,
		DelRank,
	},
	// AutoRanks
	Route{
		"GetAutoRank",
		"GET",
		"/api/autoRank/:name",
		false,
		GetAutoRank,
	},
	Route{
		"SetAutoRank",
		"POST",
		"/api/autoRank/add",
		true,
		SetAutoRank,
	},
	Route{
		"OverrideAutoRank",
		"PUT",
		"/api/autoRank/:name/override",
		true,
		SetAutoRank,
	},
	Route{
		"DelAutoRank",
		"DELETE",
		"/api/autoRank/:name/del",
		true,
		DelAutoRank,
	},
	Route{
		"AllAutoRanks",
		"GET",
		"/api/autoRank",
		false,
		GetAllAutoRanks,
	},
	// Eco (Currency)
	Route{
		"GetCurrency",
		"GET",
		"/api/eco/:name",
		false,
		GetEco,
	},
	Route{
		"SetCurrency",
		"POST",
		"/api/eco/add",
		true,
		SetEco,
	},
	Route{
		"OverrideCurrency",
		"PUT",
		"/api/eco/:name/override",
		true,
		SetEco,
	},
	Route{
		"DeleteEco",
		"DELETE",
		"/api/eco/:name/del",
		true,
		DelEco,
	},
	Route{
		"AllCurrency",
		"GET",
		"/api/eco",
		false,
		GetAllEco,
	},
	// Status
	Route{
		"GetStatus",
		"GET",
		"/api/status",
		false,
		GetServerStatus,
	},
	Route{
		"GetStatus",
		"GET",
		"/status",
		false,
		GetStatus,
	},
	Route{
		"UpdateStatus",
		"POST",
		"/api/status",
		true,
		PostStatus,
	},
	Route{
		"GetTransfer",
		"GET",
		"/api/transfer/:uuid",
		false,
		GetTransferData,
	},
	Route{
		"AddTransfer",
		"POST",
		"/api/transfer/add",
		true,
		SetTransferData,
	},
	Route{
		"OverrideTransfer",
		"PUT",
		"/api/transfer/:uuid/override",
		true,
		SetTransferData,
	},
	Route{
		"AddToken",
		"POST",
		"/api/discord/add",
		true,
		SetToken,
	},
	Route{
		"ListToken",
		"GET",
		"/api/discord/list",
		true,
		GetAllTokens,
	},
	Route{
		"GetBan",
		"GET",
		"/api/ban/:uuid",
		false,
		GetBan,
	},
	Route{
		"AddBan",
		"POST",
		"/api/ban/add",
		true,
		CreateBan,
	},
	Route{
		"GetAllBans",
		"GET",
		"/api/ban",
		false,
		GetAllBans,
	},
	Route{
		"Unban",
		"DELETE",
		"/api/ban/:uuid/del",
		true,
		DeleteBan,
	},
	Route{
		"AddAuth",
		"POST",
		"/api/auth",
		true,
		AddAuth,
	},
	Route{
		"DelAuth",
		"DELETE",
		"/api/auth",
		true,
		DelAuth,
	},
	Route{
		"DelAuth",
		"DELETE",
		"/api/auth",
		true,
		UpdateAuth,
	},
	Route{
		"AddChunkLoading",
		"POST",
		"/api/chunkloading/add",
		true,
		UpdateServerID,
	},
	Route{
		"GetChunkLoading",
		"GET",
		"/api/chunkloading/:serverID",
		false,
		GetChunkLoadingForServerID,
	},
	Route{
		"GetChunkLoading",
		"GET",
		"/api/chunkloading",
		false,
		GetAllServerChunkLoading,
	},
	Route{
		"theme.css",
		"GET",
		"/theme.css",
		false,
		GetCSS,
	},
	Route{
		"GetCommands",
		"GET",
		"/api/commands",
		true,
		GetAllCommands,
	},
	Route{
		"AddCommand",
		"POST",
		"/api/commands/add",
		true,
		addCommand,
	},
}

func Index(w http.ResponseWriter, _ *http.Request, _ mux.Params) {
	_, _ = fmt.Fprintf(w, "Welcome to the Server-Essentials Rest API v"+version)
}

func auth(pass mux.Handle) mux.Handle {
	return func(w http.ResponseWriter, r *http.Request, m mux.Params) {
		auth := r.Header.Get("token")
		if len(auth) == 0 {
			http.Error(w, "Failed to Authorize", http.StatusUnauthorized)
			return
		}
		if !validate(auth) {
			http.Error(w, "Failed to Authorize", http.StatusUnauthorized)
			return
		}
		pass(w, r, m)
	}
}

func validate(token string) bool {
	for entry := range redisDBAuth.Keys(ctx, "*").Val() {
		var authStorage AuthStorage
		json.Unmarshal([]byte(redisDBAuth.Get(ctx, redisDBAuth.Keys(ctx, "*").Val()[entry]).Val()), &authStorage)
		err := bcrypt.CompareHashAndPassword([]byte(authStorage.AuthToken), []byte(token))
		if err == nil {
			return true
		}
	}
	return false
}
