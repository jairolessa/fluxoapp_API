package com.fluxo.fluxoapp.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PreferredNameRequest(@NotBlank @Size(max = 50) String preferredName) {
}
