package com.kingpixel.cobbleutils.Model.properties;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 04/08/2024 19:40
 */
public class MinIvsProperty implements CustomPokemonProperty {
  private final String value;

  public MinIvsProperty(String value) {
    this.value = value;
  }

  @Override public @NotNull String asString() {
    return "min_ivs";
  }

  @Override public void apply(@NotNull Pokemon pokemon) {
    applyMinIvs(pokemon);
  }


  @Override public boolean matches(@NotNull Pokemon pokemon) {
    return true;
  }


  private static final List<Stats> stats = new ArrayList<>(Arrays.stream(Stats.values()).filter(stats1 -> stats1 != Stats.EVASION && stats1 != Stats.ACCURACY).toList());

  private void applyMinIvs(Pokemon pokemon) {
    if (value == null || value.isEmpty()) return;

    try {
      String[] parts = value.split("_");
      int min = Math.max(0, Math.min(Integer.parseInt(parts[0]), 31));
      int amountOfStats = Integer.parseInt(parts[1]);

      if (CobbleUtils.config.isDebug()) {
        CobbleUtils.LOGGER.info("Min -> " + min + " Amount of stats -> " + amountOfStats);
      }

      List<Stats> shuffledStats = new ArrayList<>(stats);
      java.util.Collections.shuffle(shuffledStats, Utils.RANDOM);

      for (int i = 0; i < shuffledStats.size(); i++) {
        Stats stat = shuffledStats.get(i);
        if (i < amountOfStats) {
          pokemon.getIvs().set(stat, min);
          if (CobbleUtils.config.isDebug()) {
            CobbleUtils.LOGGER.info("Setting IVs for " + pokemon.getSpecies().getName() + " to " + min + " in " + stat.getShowdownId());
          }
        } else {
          int randomIv;
          do {
            randomIv = Utils.RANDOM.nextInt(32);
          } while (randomIv == min);
          pokemon.getIvs().set(stat, randomIv);
        }
      }
    } catch (NumberFormatException e) {
      CobbleUtils.LOGGER.error("Invalid value format for MinIvsProperty: " + value);
      e.printStackTrace();
    } catch (Exception e) {
      CobbleUtils.LOGGER.error("Unexpected error in applyMinIvs");
      e.printStackTrace();
    }
  }
  @Override public void apply(@NotNull PokemonEntity pokemonEntity) {
    
  }

  @Override public boolean matches(@NotNull PokemonEntity pokemonEntity) {
    return false;
  }
}
