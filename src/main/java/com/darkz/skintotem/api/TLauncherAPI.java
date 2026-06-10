package com.darkz.skintotem.api;

import com.google.gson.*;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.skin.data.ParsedSkinData;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * API для загрузки скинов с серверов TLauncher / TL Skin Cape
 *
 * TLauncher использует собственный скин-сервер:
 *   https://auth.tlauncher.org/skin/profile/texture/login/{nickname}
 *
 * Ответ в формате base64-encoded JSON (как у Mojang sessionserver)
 *
 * Автор: Darkz | K-TEAM
 */
public class TLauncherAPI {

    private static final Gson GSON = new GsonBuilder().setLenient().create();

    // Основной TLauncher skin endpoint
    private static final String TL_SKIN_URL =
            "https://auth.tlauncher.org/skin/profile/texture/login/";

    // Fallback: TL Cape server
    private static final String TL_CAPE_URL =
            "https://cape.tlauncher.org/get.php?user=";

    /**
     * Загрузить данные скина по нику с TLauncher
     */
    public static Response<ParsedSkinData> getSkinData(String nickname) {
        int statusCode = -1;
        String responseBody = "Not reached";
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TL_SKIN_URL + nickname))
                    .header("User-Agent", "SkinTotem/1.0 (Darkz/K-TEAM)")
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            statusCode = response.statusCode();
            responseBody = response.body();

            if (statusCode == 404 || statusCode == 204) {
                return Response.empty(statusCode); // Нет скина
            }
            if (statusCode == 429) {
                return Response.empty(statusCode);
            }
            if (statusCode != 200) {
                return Response.empty(statusCode);
            }

            ParsedSkinData data = parseResponse(responseBody, nickname);
            if (data == null || data.getSkinUrl() == null) {
                return Response.empty(statusCode);
            }

            return new Response<>(statusCode, data);
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            SkinTotemClient.LOGGER.error("[TLauncherAPI] Ошибка загрузки скина {}: ", nickname, e);
            SkinTotemClient.LOGGER.error("[TLauncherAPI] Response: {}", responseBody);
        }
        return Response.empty(statusCode);
    }

    @Nullable
    private static ParsedSkinData parseResponse(String body, String nickname) {
        try {
            JsonObject root = GSON.fromJson(body, JsonObject.class);
            if (root == null) return null;

            String skinUrl  = null;
            String capeUrl  = null;
            boolean slim    = false;

            // Формат 1: прямой JSON { "skinURL": "...", "cloakURL": "...", "slim": true }
            if (root.has("skinURL")) {
                skinUrl = root.get("skinURL").getAsString();
                if (root.has("slim")) slim = root.get("slim").getAsBoolean();
                if (root.has("cloakURL")) capeUrl = root.get("cloakURL").getAsString();
                return new ParsedSkinData(skinUrl, capeUrl, null, slim);
            }

            // Формат 2: base64 (как Mojang) { "properties": [{ "name": "textures", "value": "base64..." }] }
            if (root.has("properties")) {
                for (JsonElement el : root.getAsJsonArray("properties")) {
                    JsonObject prop = el.getAsJsonObject();
                    if (!"textures".equals(prop.get("name").getAsString())) continue;
                    String b64 = prop.get("value").getAsString();
                    String decoded = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
                    JsonObject texRoot = GSON.fromJson(decoded, JsonObject.class);
                    JsonObject textures = texRoot.getAsJsonObject("textures");

                    if (textures.has("SKIN")) {
                        JsonObject skinObj = textures.getAsJsonObject("SKIN");
                        skinUrl = skinObj.get("url").getAsString();
                        if (skinObj.has("metadata")) {
                            slim = "slim".equals(skinObj.getAsJsonObject("metadata").get("model").getAsString());
                        }
                    }
                    if (textures.has("CAPE")) {
                        capeUrl = textures.getAsJsonObject("CAPE").get("url").getAsString();
                    }
                    break;
                }
                return new ParsedSkinData(skinUrl, capeUrl, null, slim);
            }

            // Формат 3: прямой URL строкой
            if (root.has("url")) {
                skinUrl = root.get("url").getAsString();
                return new ParsedSkinData(skinUrl, null, null, false);
            }

            // Формат 4: { "SKIN": { "url": "..." }, "CAPE": { "url": "..." } }
            // Именно этот формат возвращает auth.tlauncher.org
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
            if (skinUrl != null) {
                return new ParsedSkinData(skinUrl, capeUrl, null, slim);
            }

            return null;
        } catch (Exception e) {
            SkinTotemClient.LOGGER.error("[TLauncherAPI] Ошибка парсинга ответа для {}: ", nickname, e);
            return null;
        }
    }
}
