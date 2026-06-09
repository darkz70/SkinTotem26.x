package net.lopymine.mtd.api;

import com.google.gson.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.skin.data.ParsedSkinData;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;

public class ElyByAPI {

    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final String TEXTURES_URL = "https://skinsystem.ely.by/textures/";
    private static final String SKINS_URL    = "https://skinsystem.ely.by/skins/";

    public static Response<ParsedSkinData> getSkinData(String nickname) {
        int statusCode = -1;
        String responseBody = "Not reached";
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TEXTURES_URL + nickname))
                    .header("User-Agent", "SkinTotem/1.0 (Darkz/K-TEAM)")
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            statusCode = response.statusCode();
            responseBody = response.body();

            if (statusCode == 429) {
                return Response.empty(statusCode);
            }
            if (statusCode == 404) {
                return Response.empty(statusCode);
            }

            if (statusCode == 200 && responseBody != null && !responseBody.isBlank()) {
                ParsedSkinData data = parseResponse(responseBody);
                if (data != null && data.getSkinUrl() != null) {
                    return new Response<>(statusCode, data);
                }
            }

            String skinUrl = SKINS_URL + nickname + ".png";
            HttpRequest headRequest = HttpRequest.newBuilder()
                    .uri(URI.create(skinUrl))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .header("User-Agent", "SkinTotem/1.0 (Darkz/K-TEAM)")
                    .build();
            HttpResponse<Void> headResponse = client.send(headRequest, HttpResponse.BodyHandlers.discarding());

            if (headResponse.statusCode() == 200) {
                return new Response<>(200, new ParsedSkinData(skinUrl, null, null, false));
            }

            return Response.empty(statusCode);
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            MyTotemDollClient.LOGGER.error("[ElyByAPI] Ошибка загрузки скина {}: ", nickname, e);
        }
        return Response.empty(statusCode);
    }

    @Nullable
    private static ParsedSkinData parseResponse(String body) {
        try {
            JsonObject root = GSON.fromJson(body, JsonObject.class);
            if (root == null) return null;

            String skinUrl   = null;
            String capeUrl   = null;
            boolean slim     = false;

            if (root.has("SKIN")) {
                JsonObject skinObj = root.getAsJsonObject("SKIN");
                skinUrl = skinObj.has("url") ? skinObj.get("url").getAsString() : null;

                if (skinObj.has("metadata")) {
                    JsonObject meta = skinObj.getAsJsonObject("metadata");
                    if (meta.has("model")) {
                        slim = "slim".equals(meta.get("model").getAsString());
                    }
                }
            }

            if (root.has("CAPE")) {
                JsonObject capeObj = root.getAsJsonObject("CAPE");
                capeUrl = capeObj.has("url") ? capeObj.get("url").getAsString() : null;
            }

            return new ParsedSkinData(skinUrl, capeUrl, null, slim);
        } catch (Exception e) {
            MyTotemDollClient.LOGGER.error("[ElyByAPI] Ошибка парсинга ответа: ", e);
            return null;
        }
    }
}
