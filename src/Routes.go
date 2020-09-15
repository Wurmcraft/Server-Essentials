package main

import (
	"encoding/json"
	"fmt"
	mux "github.com/julienschmidt/httprouter"
	"golang.org/x/crypto/bcrypt"
	"net/http"
	"strings"
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
		"/api/users/add",
		true,
		SetGlobalUser,
	},
	Route{
		"OverrideUser",
		"PUT",
		"/api/users/:uuid/override",
		true,
		SetGlobalUser,
	},
	Route{
		"AllUsers",
		"GET",
		"/api/users",
		false,
		GetAllUsers,
	},
	// Rank
	Route{
		"GetRank",
		"GET",
		"/api/ranks/:name",
		false,
		GetRank,
	},
	Route{
		"SetRank",
		"POST",
		"/api/ranks/add",
		true,
		SetRank,
	},
	Route{
		"OverrideRank",
		"PUT",
		"/api/ranks/:name/override",
		true,
		SetRank,
	},
	Route{
		"AllRanks",
		"GET",
		"/api/ranks",
		false,
		GetAllRanks,
	},
	Route{
		"DelRank",
		"DELETE",
		"/api/ranks/:name/del",
		true,
		DelRank,
	},
	// AutoRanks
	Route{
		"GetAutoRank",
		"GET",
		"/api/autoRanks/:name",
		false,
		GetAutoRank,
	},
	Route{
		"SetAutoRank",
		"POST",
		"/api/autoRanks/add",
		true,
		SetAutoRank,
	},
	Route{
		"OverrideAutoRank",
		"PUT",
		"/api/autoRanks/:name/override",
		true,
		SetAutoRank,
	},
	Route{
		"DelAutoRank",
		"DELETE",
		"/api/autoRanks/:name/del",
		true,
		DelAutoRank,
	},
	Route{
		"AllAutoRanks",
		"GET",
		"/api/autoRanks",
		false,
		GetAllAutoRanks,
	},
	// Eco (Currency)
	Route{
		"GetCurrency",
		"GET",
		"/api/currency/:name",
		false,
		GetEco,
	},
	Route{
		"SetCurrency",
		"POST",
		"/api/currency/add",
		true,
		SetEco,
	},
	Route{
		"OverrideCurrency",
		"PUT",
		"/api/currency/:name/override",
		true,
		SetEco,
	},
	Route{
		"DeleteEco",
		"DELETE",
		"/api/currency/:name/del",
		true,
		DelEco,
	},
	Route{
		"AllCurrency",
		"GET",
		"/api/currency",
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
		"/api/bans/add",
		true,
		CreateBan,
	},
	Route{
		"GetAllBans",
		"GET",
		"/api/bans",
		false,
		GetAllBans,
	},
	Route{
		"Unban",
		"DELETE",
		"/api/bans/:uuid/del",
		true,
		DeleteBan,
	},
	Route{
		"AddAuth",
		"POST",
		"/api/auth/add",
		true,
		AddAuth,
	},
	Route{
		"DelAuth",
		"DELETE",
		"/api/auth/del",
		true,
		DelAuth,
	},
	Route{
		"UpdateAuth",
		"DELETE",
		"/api/auth/update",
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
	if len(GetPermission(token).Permission) > 0 {
		return true
	}
	return false
}

func GetPermission(token string) AuthStorage {
	for entry := range redisDBAuth.Keys(ctx, "*").Val() {
		var authStorage AuthStorage
		json.Unmarshal([]byte(redisDBAuth.Get(ctx, redisDBAuth.Keys(ctx, "*").Val()[entry]).Val()), &authStorage)
		err := bcrypt.CompareHashAndPassword([]byte(authStorage.AuthToken), []byte(token))
		if err == nil {
			return authStorage
		}
	}
	return AuthStorage{}
}

func hasPermission(user AuthStorage, typ string) bool {
	for _, b := range user.Permission {
		if strings.Compare(b, typ) == 0 {
			return true
		}
	}
	return false
}
