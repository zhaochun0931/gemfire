package org.example;

public class Country {
  protected String name;
  protected String capitol;
  protected String language;
  protected String currency;
  protected int population;

  public Country() {
    this("", "", "", "", 0);
  }

  protected Country(String name, String capitol, String language, String currency, int population) {
    this.name = name;
    this.capitol = capitol;
    this.language = language;
    this.currency = currency;
    this.population = population;
  }

  public String getName() {
    return name;
  }

  public String getCapitol() {
    return capitol;
  }

  public void setCapitol(String capitol) {
    this.capitol = capitol;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getPopulation() {
    return population;
  }

  public void setPopulation(int population) {
    this.population = population;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (name != null && !name.isEmpty()) {
      builder.append(name);
      builder.append(" (");

      if (capitol != null && !capitol.isEmpty()) {
        if (0 < builder.length() && '(' != builder.charAt(builder.length() - 1)) {
          builder.append(", ");
        }
        builder.append("Capitol: ");
        builder.append(capitol);
      }

      if (language != null && !language.isEmpty()) {
        if (0 < builder.length() && '(' != builder.charAt(builder.length() - 1)) {
          builder.append(", ");
        }
        builder.append("Language: ");
        builder.append(language);
      }

      if (currency != null && !currency.isEmpty()) {
        if (0 < builder.length() && '(' != builder.charAt(builder.length() - 1)) {
          builder.append(", ");
        }
        builder.append("Currency: ");
        builder.append(currency);
      }

      if (0 < population) {
        if (0 < builder.length() && '(' != builder.charAt(builder.length() - 1)) {
          builder.append(", ");
        }
        builder.append("Population: ");
        builder.append(population);
      }

      builder.append(")");
    }
    return builder.toString();
  }
}
