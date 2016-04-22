package com.outlook.dev.calendardemo.msgraph;

import com.outlook.dev.calendardemo.dto.GraphArray;
import com.outlook.dev.calendardemo.dto.OrgUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GraphUserService {
	@GET("/{version}/users")
	Call<GraphArray<OrgUser>> getUsers(
		@Path("version") String version,
		@Header("Authorization") String bearer
	);
}
