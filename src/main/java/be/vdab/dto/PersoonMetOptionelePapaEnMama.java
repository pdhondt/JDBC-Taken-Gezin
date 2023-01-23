package be.vdab.dto;

import java.util.Optional;

public record PersoonMetOptionelePapaEnMama(String voornaam, Optional<String> voornaamPapa, Optional<String> voornaamMama) {
}
