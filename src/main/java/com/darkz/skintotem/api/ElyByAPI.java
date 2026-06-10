package com.darkz.skintotem.api;

import com.google.gson.*;
import com.darkz.skintotem.client.SkinTotemModClient;
import com.darkz.skintotem.skin.data.ParsedSkinData;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * API для загрузки скинов с сервера Ely.by
 *
 * Endpoint: https://skinsystem.ely.by/textures/{nickname}
 * Возвращает JSON с полями SKIN, CAPE и моделью (slim/default)
 *
 * Автор: Darkz | K-TEAM |KlashRaick | LopyMine
 */
public class ElyByAPI {

    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final String TEXTURES_URL = "https://skinsystem.ely.by/textures/";
    private static final String SKINS_URL    = "https://skinsystem.ely.by/skins/";

    /**
     * Загрузить данные скина по нику с Ely.by.
     *
     * Сначала пробуем /textures/{nick} — возвращает JSON с URL и метаданными.
     * Если пришёл пустой ответ (204) или данных нет — fallback на /skins/{nick}.png напрямую.
     */
    public static Response<ParsedSkinData> getSkinData(String nickname) {
        int statusCode = -1;
        String responseBody = "Not reached";
        try {
            HttpClient client = HttpClient.newHttpClient();

            // Шаг 1: /textures/{nickname}
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

            // Пробуем распарсить JSON если статус 200 и тело не пустое
            if (statusCode == 200 && responseBody != null && !responseBody.isBlank()) {
                ParsedSkinData data = parseResponse(responseBody);
                if (data != null && data.getSkinUrl() != null) {
                    return new Response<>(statusCode, data);
                }
            }

            // Шаг 2: Fallback — /skins/{nickname}.png напрямую
            SkinTotemModClient.LOGGER.warn("[ElyByAPI] /textures/ не дал данных для {}, пробуем /skins/ fallback", nickname);
            String skinUrl = SKINS_URL + nickname + ".png";

            // Проверяем что URL существует
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
            SkinTotemModClient.LOGGER.error("[ElyByAPI] Ошибка загрузки скина {}: ", nickname, e);
            SkinTotemModClient.LOGGER.error("[ElyByAPI] Response: {}", responseBody);
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

            // Ely.by возвращает { "SKIN": { "url": "...", "metadata": { "model": "slim" } }, "CAPE": { "url": "..." } }
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
            SkinTotemModClient.LOGGER.error("[ElyByAPI] Ошибка парсинга ответа: ", e);
            return null;
        }
    }
        }
