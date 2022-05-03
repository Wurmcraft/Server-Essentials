/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.rest.crud;

import io.javalin.core.validation.BodyValidator;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.db.DatabaseConnector;
import io.wurmatron.server_essentials.backend.model.db.UserAccount;
import io.wurmatron.server_essentials.backend.model.rest.RestResponse;
import io.wurmatron.server_essentials.backend.rest.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import joptsimple.internal.Strings;
import kotlin.collections.ArrayDeque;
import org.jetbrains.annotations.NotNull;

public class UserRoutes {

  @OpenApi(
      description = "Create a new user account",
      summary = "Create a new user account with the provided information",
      deprecated = false,
      tags = {"User"},
      headers = {},
      requestBody =
          @OpenApiRequestBody(
              content = @OpenApiContent(from = UserAccount.class),
              description = "User account to be created"),
      responses = {
        @OpenApiResponse(
            status = "201",
            content = @OpenApiContent(from = RestResponse.class),
            description = "User account has been created"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "There is an error with the provided user account"),
        @OpenApiResponse(
            status = "401",
            content = @OpenApiContent(from = RestResponse.class),
            description = "No credentials where provided to preform this action"),
        @OpenApiResponse(
            status = "403",
            content = @OpenApiContent(from = RestResponse.class),
            description = "The provided credentials do not have permission to preform this action"),
        @OpenApiResponse(
            status = "409",
            content = @OpenApiContent(from = RestResponse.class),
            description = "A user account with the provided information already exists"),
        @OpenApiResponse(
            status = "429",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The provided credentials have created to many user accounts within a given time frame"),
        @OpenApiResponse(
            status = "500",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The api had an internal server error, see the response for more information"),
      })
  @Route(path = "/api/users", method = "POST")
  public static Handler createUser =
      ctx -> {
        UserAccount userAccount = validateUserAccount(ctx.bodyValidator(UserAccount.class));
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        UserAccount existingAccount =
            DatabaseConnector.getSession()
                .getCurrentSession()
                .get(UserAccount.class, userAccount.getUuid());
        DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
        if (existingAccount != null) {
          ctx.result(ServerEssentialsBackend.GSON.toJson(filterBasedOnPerms(existingAccount)))
              .status(409);
        }
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        DatabaseConnector.getSession().getCurrentSession().save(userAccount);
        DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
        ctx.status(201);
      };

  @OpenApi(
      description = "Delete a existing account",
      summary = "Delete an existing account with the provided uuid",
      deprecated = false,
      tags = {"User"},
      headers = {},
      pathParams = {
        @OpenApiParam(name = "uuid", required = true, description = "UUID of the account to delete")
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = UserAccount.class),
            description = "User account has been deleted"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An error occurred while attempting to delete the provided user account"),
        @OpenApiResponse(
            status = "401",
            content = @OpenApiContent(from = RestResponse.class),
            description = "No credentials where provided to preform this action"),
        @OpenApiResponse(
            status = "403",
            content = @OpenApiContent(from = RestResponse.class),
            description = "The provided credentials do not have permission to preform this action"),
        @OpenApiResponse(
            status = "404",
            content = @OpenApiContent(from = RestResponse.class),
            description = "No user account exists with this uuid"),
        @OpenApiResponse(
            status = "429",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The provided credentials have deleted to many user accounts within a given time frame"),
        @OpenApiResponse(
            status = "500",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The api had an internal server error, see the response for more information"),
      })
  @Route(path = "/api/users/{uuid}", method = "DELETE")
  public static Handler deleteUser =
      ctx -> {
        String uuid =
            ctx.pathParamAsClass("uuid", String.class)
                .check(
                    id -> {
                      try {
                        UUID.fromString(id);
                        return true;
                      } catch (Exception e) {
                        return false;
                      }
                    },
                    "UUID must be valid")
                .get();
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        UserAccount userAccount =
            DatabaseConnector.getSession().getCurrentSession().get(UserAccount.class, uuid);
        if (userAccount != null) {
          DatabaseConnector.getSession().getCurrentSession().delete(userAccount);
          DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
          ctx.result(ServerEssentialsBackend.GSON.toJson(userAccount));
          ctx.status(200);
        } else {
          ctx.status(404);
        }
      };

  @OpenApi(
      description = "Get an existing user account",
      summary = "Get an existing user account with the provided uuid",
      deprecated = false,
      tags = {"User"},
      headers = {},
      pathParams = {
        @OpenApiParam(
            name = "uuid",
            required = true,
            description = "UUID of the account you are looking for")
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = UserAccount.class),
            description = "User account has been returned"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An error occurred while attempting to get the provided user account"),
        @OpenApiResponse(
            status = "401",
            content = @OpenApiContent(from = RestResponse.class),
            description = "No credentials where provided to preform this action"),
        @OpenApiResponse(
            status = "403",
            content = @OpenApiContent(from = RestResponse.class),
            description = "The provided credentials do not have permission to preform this action"),
        @OpenApiResponse(
            status = "404",
            content = @OpenApiContent(from = RestResponse.class),
            description = "No user account exists with this uuid"),
        @OpenApiResponse(
            status = "429",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The provided credentials have deleted to many user accounts within a given time frame"),
        @OpenApiResponse(
            status = "500",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The api had an internal server error, see the response for more information"),
      })
  @Route(path = "/api/users/{uuid}", method = "GET")
  public static Handler getUser =
      ctx -> {
        String uuid =
            ctx.pathParamAsClass("uuid", String.class)
                .check(
                    id -> {
                      try {
                        UUID.fromString(id);
                        return true;
                      } catch (Exception e) {
                        return false;
                      }
                    },
                    "UUID must be valid")
                .get();
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        UserAccount userAccount =
            DatabaseConnector.getSession().getCurrentSession().get(UserAccount.class, uuid);
        if (userAccount != null) {
          DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
          ctx.result(ServerEssentialsBackend.GSON.toJson(filterBasedOnPerms(userAccount)));
          ctx.status(200);
        } else {
          ctx.status(404);
        }
      };

  @OpenApi(
      description = "Get a list of user accounts that match the provided filters",
      summary = "Get a list of user accounts that match the provided query filters",
      deprecated = false,
      tags = {"User"},
      headers = {},
      queryParams = {
        @OpenApiParam(name = "uuid", description = "Filter based on a partial uuid"),
        @OpenApiParam(name = "username", description = "Filter based on the last known username"),
        @OpenApiParam(name = "rank", description = "Filter based on the users rank"),
        @OpenApiParam(name = "perm", description = "Filter based on the accounts perms"),
        @OpenApiParam(name = "lang", description = "Filter based on the accounts language key"),
        @OpenApiParam(
            name = "muted",
            description = "Filter based on the accounts mute status",
            type = Boolean.class),
        @OpenApiParam(name = "nick", description = "Filter based on the accounts nickname"),
        @OpenApiParam(name = "discord-id", description = "Filter based on the accounts discord-id"),
        @OpenApiParam(
            name = "server-playtime",
            description = "Filter based on the accounts server playtime"),
        @OpenApiParam(
            name = "total-playtime",
            description = "Filter based on the accounts total playtime"),
        @OpenApiParam(
            name = "currency-balance",
            description = "Filter based on the accounts currency balance"),
        @OpenApiParam(
            name = "total-balance",
            description = "Filter based on the accounts total balance"),
        @OpenApiParam(
            name = "system-perms",
            description = "Filter based on the accounts system perms"),
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = UserAccount[].class),
            description = "User accounts that match the provided query filters"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An error occurred while applying the requested filters"),
        @OpenApiResponse(
            status = "401",
            content = @OpenApiContent(from = RestResponse.class),
            description = "No credentials where provided to preform this action"),
        @OpenApiResponse(
            status = "403",
            content = @OpenApiContent(from = RestResponse.class),
            description = "The provided credentials do not have permission to preform this action"),
        @OpenApiResponse(
            status = "409",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An invalid combo of filters has been applied"),
        @OpenApiResponse(
            status = "429",
            content = @OpenApiContent(from = RestResponse.class),
            description = "Too many requested filters for the provided authentication token"),
        @OpenApiResponse(
            status = "500",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The api had an internal server error, see the response for more information"),
      })
  @Route(path = "/api/users/", method = "GET")
  public static Handler getUsers =
      ctx -> {
        CriteriaBuilder cb = DatabaseConnector.getSession().getCriteriaBuilder();
        CriteriaQuery<UserAccount> cr = cb.createQuery(UserAccount.class);
        Root<UserAccount> root = cr.from(UserAccount.class);
        cr = cr.select(root);
        List<Predicate> filters = createFilters(ctx, cb, root);
        // Process query
        cr = cr.where(filters.toArray(new Predicate[] {}));
        List<UserAccount> filteredAccounts =
            DatabaseConnector.getSession().createEntityManager().createQuery(cr).getResultList();
        List<UserAccount> protectedAccounts = new ArrayDeque<>();
        for (UserAccount account : filteredAccounts) {
          protectedAccounts.add(filterBasedOnPerms(account));
        }
        ctx.result(
                ServerEssentialsBackend.GSON.toJson(protectedAccounts.toArray(new UserAccount[0])))
            .status(200);
      };

  @NotNull
  private static List<Predicate> createFilters(
      Context ctx, CriteriaBuilder cb, Root<UserAccount> root) {
    List<Predicate> filters = new ArrayList<>();

    filters.add(createFilter(ctx, "uuid", cb, root, "uuid"));
    filters.add(createFilter(ctx, "username", cb, root, "lastUsername"));
    filters.add(createFilter(ctx, "rank", cb, root, "ranks"));
    filters.add(createFilter(ctx, "perm", cb, root, "perms"));
    filters.add(createFilter(ctx, "perk", cb, root, "perks"));
    filters.add(createFilter(ctx, "lang", cb, root, "language"));
    filters.add(createFilter(ctx, "nick", cb, root, "nickname"));
    filters.add(createFilter(ctx, "discord-id", cb, root, "discord_id"));
    filters.add(createFilter(ctx, "system-perms", cb, root, "system_perms"));

    // Filter muted
    String muted = ctx.queryParam("muted");
    if (muted != null && muted.length() > 0) {
      filters.add(cb.equal(root.get("muted"), Boolean.getBoolean(muted)));
    }

    // Filter server playtime // TODO Implement
    String serverPlaytime = ctx.queryParam("server-playtime");
    if (serverPlaytime != null && serverPlaytime.length() > 0) {
      ctx.status(502);
    }

    // Filter total playtime // TODO Implement
    String totalPlaytime = ctx.queryParam("total-playtime");
    if (totalPlaytime != null && totalPlaytime.length() > 0) {
      ctx.status(502);
    }

    // Filter server balance // TODO Implement
    String serverBalance = ctx.queryParam("server-balance");
    if (serverBalance != null && serverBalance.length() > 0) {
      ctx.status(502);
    }

    // Filter total balance // TODO Implement
    String totalBalance = ctx.queryParam("totalBalance");
    if (totalBalance != null && totalBalance.length() > 0) {
      ctx.status(502);
    }

    return filters;
  }

  private static Predicate createFilter(
      Context ctx, String key, CriteriaBuilder cb, Root<UserAccount> root, String value) {
    String filterQuery = ctx.queryParam(key);
    if (filterQuery != null && filterQuery.length() > 0) {
      return cb.like(root.get(value), "%" + filterQuery + "%");
    }
    return null;
  }

  @OpenApi(
      description = "Update an existing user account",
      summary = "Update an existing user account (as a whole)",
      deprecated = false,
      tags = {"User"},
      headers = {},
      requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = UserAccount.class)}),
      pathParams = {
        @OpenApiParam(name = "uuid", required = true, description = "UUID of the account to update")
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = UserAccount.class),
            description = "User accounts has been updated"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An error occurred while applying the requested filters"),
        @OpenApiResponse(
            status = "401",
            content = @OpenApiContent(from = RestResponse.class),
            description = "No credentials where provided to preform this action"),
        @OpenApiResponse(
            status = "403",
            content = @OpenApiContent(from = RestResponse.class),
            description = "The provided credentials do not have permission to preform this action"),
        @OpenApiResponse(
            status = "409",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An invalid combo of filters has been applied"),
        @OpenApiResponse(
            status = "429",
            content = @OpenApiContent(from = RestResponse.class),
            description = "Too many requested filters for the provided authentication token"),
        @OpenApiResponse(
            status = "500",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The api had an internal server error, see the response for more information"),
      })
  @Route(path = "/api/users/{uuid}", method = "PUT")
  public static Handler updateUser =
      ctx -> {
        String uuid =
            ctx.pathParamAsClass("uuid", String.class)
                .check(
                    id -> {
                      try {
                        UUID.fromString(id);
                        return true;
                      } catch (Exception e) {
                        return false;
                      }
                    },
                    "UUID must be valid")
                .get();
        UserAccount updateAccount = validateUserAccount(ctx.bodyValidator(UserAccount.class));
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        UserAccount userAccount =
            DatabaseConnector.getSession().getCurrentSession().get(UserAccount.class, uuid);
        DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        if (userAccount != null) {
          DatabaseConnector.getSession().getCurrentSession().update(updateAccount);
          DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
          ctx.result(ServerEssentialsBackend.GSON.toJson(updateAccount));
          ctx.status(200);
        } else {
          ctx.status(404);
        }
      };

  public static final String[] validPatches =
      new String[] {
        "username",
        "lang",
        "muted",
        "nick",
        "discord-id",
        "password-hash",
        "password-salt",
        "wallet",
        "playtime",
        "ranks",
        "perms",
        "perks",
        "system-perms"
      };

  @OpenApi(
      description = "Update an existing user account",
      summary = "Update an existing user account (single value only)",
      deprecated = false,
      tags = {"User"},
      headers = {},
      requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = UserAccount.class)}),
      pathParams = {
        @OpenApiParam(
            name = "uuid",
            required = true,
            description = "UUID of the account to update"),
        @OpenApiParam(
            name = "patch",
            required = true,
            description =
                "Type of data to update, ('username', 'lang', 'muted', 'nick', 'discord-id', 'password-hash', 'password-salt', 'wallet', 'playtime', 'ranks', 'perms', 'perks', 'system-perms')")
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = UserAccount.class),
            description = "User accounts has been updated"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An error occurred while applying the requested filters"),
        @OpenApiResponse(
            status = "401",
            content = @OpenApiContent(from = RestResponse.class),
            description = "No credentials where provided to preform this action"),
        @OpenApiResponse(
            status = "403",
            content = @OpenApiContent(from = RestResponse.class),
            description = "The provided credentials do not have permission to preform this action"),
        @OpenApiResponse(
            status = "429",
            content = @OpenApiContent(from = RestResponse.class),
            description = "Too many requested filters for the provided authentication token"),
        @OpenApiResponse(
            status = "500",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The api had an internal server error, see the response for more information"),
      })
  @Route(path = "/api/users/{uuid}/{patch}", method = "PATCH")
  public static Handler updateUserSpecific =
      ctx -> {
        // Make sure uuid is valid
        String uuid =
            ctx.pathParamAsClass("uuid", String.class)
                .check(
                    id -> {
                      try {
                        UUID.fromString(id);
                        return true;
                      } catch (Exception e) {
                        return false;
                      }
                    },
                    "UUID must be valid")
                .get();
        // Validate patch value
        String patch = ctx.pathParam("patch");
        if (!isValidPatch(patch)) {
          ctx.result(
                  ServerEssentialsBackend.GSON.toJson(
                      new RestResponse(
                          patch + " is not a valid patch",
                          "Patch must be valid ('",
                          Strings.join(validPatches, "', '") + "')")))
              .status(400);
        }
        // Get existing account data
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        UserAccount existingAccountData =
            DatabaseConnector.getSession().getCurrentSession().get(UserAccount.class, uuid);
        // Get body and verify uuid's match
        UserAccount updateData = ctx.bodyAsClass(UserAccount.class);
        if (!updateData.getUuid().equals(uuid)) {
          ctx.result(
                  ServerEssentialsBackend.GSON.toJson(
                      new RestResponse(
                          "UUID's must match",
                          "UUID's from the path and the body must match!",
                          ServerEssentialsBackend.GSON.toJson(updateData))))
              .status(400);
        }
        // Update existing data with provided details
        if (existingAccountData != null) {
          UserAccount updatedAccount =
              patchAccountWithUpdate(existingAccountData, updateData, patch);
          DatabaseConnector.getSession().getCurrentSession().update(updatedAccount);
          DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
          ctx.result(ServerEssentialsBackend.GSON.toJson(updatedAccount));
          ctx.status(200);
        } else {
          ctx.status(404);
        }
      };

  private static UserAccount validateUserAccount(BodyValidator<UserAccount> account) {
    return account
        .check(usr -> usr.getUuid() != null, "UUID must not be null")
        .check(usr -> usr.getUuid().length() > 0, "UUID must not be empty")
        .check(
            usr -> {
              try {
                UUID.fromString(usr.getUuid());
                return true;
              } catch (Exception e) {
                return false;
              }
            },
            "UUID must be valid")
        .check(usr -> usr.getLastUsername().length() > 0, "Username must not be empty")
        .get();
  }

  // TODO Filter based on requester permissions / account type
  private static UserAccount filterBasedOnPerms(UserAccount account) {
    account.setPasswordHash(null);
    account.setPasswordSalt(null);
    account.setSystemPermissions(null);
    return account;
  }

  private static UserAccount patchAccountWithUpdate(
      UserAccount existingAccount, UserAccount providedUpdateData, String patchValue) {
    if (patchValue.equalsIgnoreCase("username")) {
      existingAccount.setLastUsername(providedUpdateData.getLastUsername());
    } else if (patchValue.equalsIgnoreCase("lang")) {
      existingAccount.setLanguage(providedUpdateData.getLanguage());
    } else if (patchValue.equalsIgnoreCase("muted")) {
      existingAccount.setMuted(providedUpdateData.isMuted());
    } else if (patchValue.equalsIgnoreCase("nick")) {
      existingAccount.setNickname(providedUpdateData.getNickname());
    } else if (patchValue.equalsIgnoreCase("discord-id")) {
      existingAccount.setDiscordID(providedUpdateData.getDiscordID());
    } else if (patchValue.equalsIgnoreCase("password-hash")) {
      existingAccount.setPasswordHash(providedUpdateData.getPasswordHash());
    } else if (patchValue.equalsIgnoreCase("password-salt")) {
      existingAccount.setPasswordSalt(providedUpdateData.getPasswordSalt());
    } else if (patchValue.equalsIgnoreCase("wallet")) {
      existingAccount.setBankAccounts(providedUpdateData.getBankAccounts());
    } else if (patchValue.equalsIgnoreCase("playtime")) {
      existingAccount.setPlayTime(providedUpdateData.getPlayTime());
    } else if (patchValue.equalsIgnoreCase("ranks")) {
      existingAccount.setRanks(providedUpdateData.getRanks());
    } else if (patchValue.equalsIgnoreCase("perms")) {
      existingAccount.setPerms(providedUpdateData.getPerms());
    } else if (patchValue.equalsIgnoreCase("perks")) {
      existingAccount.setPurchasedPerks(providedUpdateData.getPurchasedPerks());
    } else if (patchValue.equalsIgnoreCase("system-perms")) {
      existingAccount.setSystemPermissions(providedUpdateData.getSystemPermissions());
    }
    return existingAccount;
  }

  private static boolean isValidPatch(String patch) {
    for (String valid : validPatches) {
      if (patch.equalsIgnoreCase(valid)) {
        return true;
      }
    }
    return false;
  }
}
