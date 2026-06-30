package model.football;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatchStatus(
        @JsonProperty("text")
        String text,
        @JsonProperty("clock")
        String clock
) {}
