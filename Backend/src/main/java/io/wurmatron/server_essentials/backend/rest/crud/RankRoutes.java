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
import io.wurmatron.server_essentials.backend.model.db.Rank;
import io.wurmatron.server_essentials.backend.model.db.UserAccount;
import io.wurmatron.server_essentials.backend.model.rest.RestResponse;
import io.wurmatron.server_essentials.backend.rest.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;

public class RankRoutes {

  public static final String NAME_REGEX = "[a-zA-Z0-9]+";

  @OpenApi(
      description = "Create a new rank",
      summary = "Create a new rank with the provided information",
      deprecated = false,
      tags = {"Rank"},
      headers = {},
      requestBody =
          @OpenApiRequestBody(
              content = @OpenApiContent(from = Rank.class),
              description = "Rank to be created"),
      responses = {
        @OpenApiResponse(
            status = "201",
            content = @OpenApiContent(from = RestResponse.class),
            description = "Rank has been created"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "There is an error with the provided rank"),
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
            description = "A rank with the same name already exists"),
        @OpenApiResponse(
            status = "429",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The provided credentials has created to many ranks within a given time frame"),
        @OpenApiResponse(
            status = "500",
            content = @OpenApiContent(from = RestResponse.class),
            description =
                "The api had an internal server error, see the response for more information"),
      })
  @Route(path = "api/ranks", method = "POST")
  public static Handler createRank =
      ctx -> {
        Rank rank = validateRank(ctx.bodyValidator(Rank.class));
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        Rank existingRank =
            DatabaseConnector.getSession().getCurrentSession().get(Rank.class, rank.getName());
        DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
        if (existingRank != null) {
          ctx.result(ServerEssentialsBackend.GSON.toJson(existingRank)).status(409);
        }
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        DatabaseConnector.getSession().getCurrentSession().save(rank);
        DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
        ctx.status(201);
      };

  @OpenApi(
      description = "Update an existing rank",
      summary = "Update an existing rank",
      deprecated = false,
      tags = {"Rank"},
      headers = {},
      requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Rank.class)}),
      pathParams = {
        @OpenApiParam(name = "name", required = true, description = "name of the rank to update")
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = Rank.class),
            description = "Rank has been updated"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An error occurred while trying to update the existing rank"),
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
            description = "A rank with this name does not exist"),
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
  @Route(path = "api/ranks/{name}", method = "PUT")
  public static Handler updateRank =
      ctx -> {
        Rank rank = validateRank(ctx.bodyValidator(Rank.class));
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        Rank existingRank =
            DatabaseConnector.getSession().getCurrentSession().get(Rank.class, rank.getName());
        DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
        if (existingRank == null) {
          ctx.status(404);
          return;
        }
        if (!rank.getName().equals(ctx.pathParam("name"))) {
          ctx.result(
                  ServerEssentialsBackend.GSON.toJson(
                      new RestResponse(
                          "Name must be the same", "Path and body name must be the same", "")))
              .status(400);
          return;
        }
        Objects.requireNonNull(DatabaseConnector.getSession())
            .getCurrentSession()
            .beginTransaction();
        DatabaseConnector.getSession().getCurrentSession().update(rank);
        DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
        ctx.result(ServerEssentialsBackend.GSON.toJson(rank)).status(200);
      };

  @OpenApi(
      description = "Get an existing rank",
      summary = "Get an existing rank based on its name",
      deprecated = false,
      tags = {"Rank"},
      headers = {},
      pathParams = {
        @OpenApiParam(
            name = "name",
            required = true,
            description = "name of the rank you are looking for")
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = Rank.class),
            description = "Rank has been returned"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An error occurred while attempting to get the provided rank"),
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
            description = "No rank exists with this name"),
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
  @Route(path = "api/ranks/{name}", method = "GET")
  public static Handler getRank =
      ctx -> {
        String name = ctx.pathParam("name");
        if (name.length() > 0 && name.matches(NAME_REGEX)) {
          Objects.requireNonNull(DatabaseConnector.getSession())
              .getCurrentSession()
              .beginTransaction();
          Rank rank = DatabaseConnector.getSession().getCurrentSession().get(Rank.class, name);
          DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
          ctx.result(ServerEssentialsBackend.GSON.toJson(rank)).status(200);
        } else {
          ctx.result(
              ServerEssentialsBackend.GSON.toJson(
                  new RestResponse("Name must be alpha-numeric", "(A->Z, 0 -> 9)", "")));
        }
      };

  @OpenApi(
      description = "Get a list of ranks that match the provided filters",
      summary = "Get a list of ranks that match the provided query filters",
      deprecated = false,
      tags = {"Rank"},
      headers = {},
      queryParams = {
        @OpenApiParam(name = "name", description = "Filter based on a partial name"),
        @OpenApiParam(name = "permissions", description = "Filter based on a permissions"),
        @OpenApiParam(name = "inheritance", description = "Filter based on a inheritance"),
        @OpenApiParam(name = "prefix", description = "Filter based on a partial or full prefix"),
        @OpenApiParam(name = "suffix", description = "Filter based on a partial or full suffix"),
        @OpenApiParam(name = "prefix-priority", description = "Filter based on a prefix priority"),
        @OpenApiParam(name = "suffix-priority", description = "Filter based on a suffix priority"),
        @OpenApiParam(name = "color", description = "Filter based on a rank color"),
        @OpenApiParam(name = "color-priority", description = "Filter based on a color priority"),
        @OpenApiParam(
            name = "active-servers",
            description = "Filter based on a its active servers"),
        @OpenApiParam(
            name = "protected",
            description = "Filter based on a if the rank is protected or not"),
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = UserAccount[].class),
            description = "Ranks that match the provided query filters"),
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
  @Route(path = "api/ranks", method = "GET")
  public static Handler getRanks =
      ctx -> {
        CriteriaBuilder cb = DatabaseConnector.getSession().getCriteriaBuilder();
        CriteriaQuery<Rank> cr = cb.createQuery(Rank.class);
        Root<Rank> root = cr.from(Rank.class);
        cr = cr.select(root);
        List<Predicate> filters = createFilters(ctx, cb, root);
        // Process query
        cr = cr.where(filters.toArray(new Predicate[] {}));
        try {
          EntityManager entityManger = DatabaseConnector.getSession().createEntityManager();
          TypedQuery<Rank> query = entityManger.createQuery(cr);
          List<Rank> filteredAccounts = query.getResultList();
          ctx.result(ServerEssentialsBackend.GSON.toJson(filteredAccounts.toArray(new Rank[0])))
              .status(200);
        } catch (Exception e) {
          e.printStackTrace();
        }
      };

  @OpenApi(
      description = "Delete a existing rank",
      summary = "Delete an existing rank with the provided name",
      deprecated = false,
      tags = {"Rank"},
      headers = {},
      pathParams = {
        @OpenApiParam(
            name = "name",
            required = true,
            description = "Name of the rank to be deleted")
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = @OpenApiContent(from = Rank.class),
            description = "Rank has been deleted"),
        @OpenApiResponse(
            status = "400",
            content = @OpenApiContent(from = RestResponse.class),
            description = "An error occurred while attempting to delete the rank"),
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
            description = "No rank exists with this name"),
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
  @Route(path = "api/ranks/{name}", method = "DELETE")
  public static Handler deleteRank =
      ctx -> {
        String name = ctx.pathParam("name");
        if (name.length() > 0 && name.matches(NAME_REGEX)) {
          Objects.requireNonNull(DatabaseConnector.getSession())
              .getCurrentSession()
              .beginTransaction();
          Rank rank = DatabaseConnector.getSession().getCurrentSession().get(Rank.class, name);
          DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
          if (rank != null) {
            Objects.requireNonNull(DatabaseConnector.getSession())
                .getCurrentSession()
                .beginTransaction();
            DatabaseConnector.getSession().getCurrentSession().delete(rank);
            DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
            ctx.result(ServerEssentialsBackend.GSON.toJson(rank)).status(200);
          } else {
            ctx.status(404);
          }
        } else {
          ctx.result(
              ServerEssentialsBackend.GSON.toJson(
                  new RestResponse("Name must be alpha-numeric", "(A->Z, 0 -> 9)", "")));
        }
      };

  private static Rank validateRank(BodyValidator<Rank> rank) {
    return rank.check(rnk -> rnk.getName() != null, "Name must not be empty")
        .check(rnk -> rnk.getName().length() > 0, "Name must be longer than 0 characters")
        .check(rnk -> rnk.getName().matches(NAME_REGEX), "Name must only contain letters, numbers")
        .get();
  }

  private static Predicate createFilter(
      Context ctx, String key, CriteriaBuilder cb, Root<Rank> root, String value) {
    String filterQuery = ctx.queryParam(key);
    if (filterQuery != null && filterQuery.length() > 0) {
      return cb.like(root.get(value), "%" + filterQuery + "%");
    }
    return null;
  }

  @NotNull
  private static List<Predicate> createFilters(Context ctx, CriteriaBuilder cb, Root<Rank> root) {
    List<Predicate> filters = new ArrayList<>();
    filters.add(createFilter(ctx, "name", cb, root, "name"));
    filters.add(createFilter(ctx, "permissions", cb, root, "permissions"));
    filters.add(createFilter(ctx, "inheritance", cb, root, "inheritance"));
    filters.add(createFilter(ctx, "prefix", cb, root, "prefix"));
    filters.add(createFilter(ctx, "suffix", cb, root, "suffix"));
    filters.add(createFilter(ctx, "prefix-priority", cb, root, "prefix_priority"));
    filters.add(createFilter(ctx, "suffix-priority", cb, root, "suffix_priority"));
    filters.add(createFilter(ctx, "color", cb, root, "color"));
    filters.add(createFilter(ctx, "color-priority", cb, root, "color_priority"));
    filters.add(createFilter(ctx, "active-servers", cb, root, "active_servers"));
    filters.add(createFilter(ctx, "protected", cb, root, "protected"));
    List<Predicate> nonNullFilters = new ArrayList<>();
    for (Predicate predicate : filters) {
      if (predicate != null) {
        nonNullFilters.add(predicate);
      }
    }
    return nonNullFilters;
  }
}
