package model.football;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FootballMatch(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("date") String date,
        @JsonProperty("status") MatchStatus status,
        @JsonProperty("score") ScoreInfo score
) {}
