package com.diameter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class GsonConfig {

	@Bean
	public Gson gson() {

		return new GsonBuilder()

				// LocalDate Adapter
				.registerTypeAdapter(LocalDate.class,
						(JsonDeserializer<LocalDate>) (json, type, context) ->
								LocalDate.parse(json.getAsString()))

				// LocalDateTime Adapter
				.registerTypeAdapter(LocalDateTime.class,
						(JsonDeserializer<LocalDateTime>) (json, type, context) ->
								LocalDateTime.parse(json.getAsString()))

				// ✅ FIX: Boolean → Short converter
				.registerTypeAdapter(Short.class,
						(JsonDeserializer<Short>) (json, type, context) -> {

							if (json.isJsonPrimitive()) {

								if (json.getAsJsonPrimitive().isBoolean()) {
									return (short) (json.getAsBoolean() ? 1 : 0);
								}

								return json.getAsShort();
							}

							return null;
						})

				.create();
	}
}