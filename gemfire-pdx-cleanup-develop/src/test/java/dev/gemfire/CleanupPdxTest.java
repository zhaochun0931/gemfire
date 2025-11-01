/*
 * Copyright 2025 Broadcom. All rights reserved.
 */

package dev.gemfire;

import static org.apache.geode.distributed.ConfigurationProperties.BIND_ADDRESS;
import static org.apache.geode.distributed.ConfigurationProperties.DISTRIBUTED_SYSTEM_ID;
import static org.apache.geode.distributed.ConfigurationProperties.REMOTE_LOCATORS;
import static org.apache.geode.distributed.ConfigurationProperties.SERIALIZABLE_OBJECT_FILTER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.control.RebalanceResults;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.json.JsonDocument;
import org.apache.geode.json.StorageFormat;
import org.apache.geode.json.internal.JsonDocumentFactoryCreator;
import org.apache.geode.logging.internal.log4j.api.LogService;
import org.apache.geode.pdx.internal.PdxType;
import org.apache.geode.pdx.internal.PeerTypeRegistration;
import org.apache.geode.test.dunit.rules.ClusterStartupRule;
import org.apache.geode.test.dunit.rules.MemberVM;
import org.apache.geode.test.dunit.rules.SerializableFunction;
import org.apache.geode.test.junit.rules.GfshCommandRule;
import org.apache.geode.test.junit.rules.ServerStarterRule;

public class CleanupPdxTest implements Serializable {

  private static final Logger logger = LogService.getLogger();
  private static final String REGION_NAME = "json_region";

  @ClassRule
  public static TemporaryFolder baseDir = new TemporaryFolder();

  @Rule
  public transient ClusterStartupRule cluster = new ClusterStartupRule();

  @ClassRule
  public static GfshCommandRule gfsh = new GfshCommandRule();

  private static SerializableFunction<ServerStarterRule> startupFn;
  private transient MemberVM locator, server1, server2;

  @Before
  public void setup() throws Exception {
    locator = cluster.startLocatorVM(0, x -> x
        .withProperty(DISTRIBUTED_SYSTEM_ID, "1")
        .withProperty(REMOTE_LOCATORS, "localhost[10334]"));
    gfsh.connectAndVerify(locator);
    gfsh.executeAndAssertThat("configure pdx --read-serialized=true")
        .statusIsSuccess();

    int locatorPort = locator.getPort();
    startupFn = x -> x
        .withConnectionToLocator(locatorPort)
        .withSystemProperty(DISTRIBUTED_SYSTEM_ID, "1");

    server1 = cluster.startServerVM(1, startupFn);
    server2 = cluster.startServerVM(2, startupFn);

    server1.invoke(() -> FunctionService.registerFunction(new CleanupPdxTypes()));
    server2.invoke(() -> FunctionService.registerFunction(new CleanupPdxTypes()));

    gfsh.executeAndAssertThat("create gateway-sender --id=GW-SENDER --remote-distributed-system-id=2")
        .statusIsSuccess();
    gfsh.executeAndAssertThat("create region --name=" + REGION_NAME + " --type=PARTITION_REDUNDANT --gateway-sender-id=GW-SENDER")
        .statusIsSuccess();
  }

  @Test
  public void testPdxTypeCleanup() {
    int entryCount = server1.invoke("Populating region", () -> {
      Region<Integer, JsonDocument> region = ClusterStartupRule.getCache().getRegion(REGION_NAME);
      List<List<String>> allCombos = generateAllValueCombos(8);

      long now;
      int key = 0;
      int entriesToCreate = allCombos.size();

      for (List<String> combo : allCombos) {
        String json = createJsonString(key, combo, 0);
        now = System.currentTimeMillis();
        region.put(key, new JsonDocumentFactoryCreator().create(StorageFormat.PDX)
            .create(json));
        logger.info("DEBUG: insertion time for key {} = {}ms", key,
            System.currentTimeMillis() - now);
        key++;
        entriesToCreate--;
        if (entriesToCreate == 0) {
          break;
        }
      }

      return allCombos.size();
    });

    // Clean out some PdxTypes so that we have entries without a type
    int entriesToDelete = 10;
    server1.invoke("Removing PDX types", () -> {
      Region<Integer, PdxType> pdxTypeRegion = ClusterStartupRule.getCache()
          .getRegion(PeerTypeRegistration.REGION_NAME);
      int i = 0;
      for (Integer id : pdxTypeRegion.keySet()) {
        pdxTypeRegion.destroy(id);
        System.err.println("DEBUG: Removed pdxType " + id);
        if (++i == entriesToDelete) {
          break;
        }
      }
      return entriesToDelete;
    });

    MemberVM server3 = cluster.startServerVM(3, locator.getPort());
    server3.invoke(() -> {
      FunctionService.registerFunction(new CleanupPdxTypes());
      RebalanceResults rebalanceResults = ClusterStartupRule.getCache().getResourceManager()
          .createRebalanceFactory()
          .start()
          .getResults();
      System.err.println(rebalanceResults);
    });

//    gfsh.executeAndAssertThat(String.format("execute function --member=server-2 --id=%1$s --arguments=false,%2$s",
    gfsh.executeAndAssertThat(String.format("execute function --id=%1$s --arguments=false,%2$s",
            CleanupPdxTypes.ID, REGION_NAME))
        .statusIsSuccess();
  }

  private static String createJsonString(int assetId, List<String> combo, int extraFields) {
    StringBuilder json = new StringBuilder();
    json.append("{\"assetId\":").append(assetId).append(",");

    for (int i = 0; i < combo.size(); i++) {
      json.append("\"field_").append(i).append("\":");

      if (combo.get(i) != null) {
        json.append("\"").append(combo.get(i)).append("\"");
      } else {
        json.append("true");
      }
      if (i < (combo.size() - 1 + extraFields)) {
        json.append(",");
      }
    }

    for (int i = 0; i < extraFields; i++) {
      json.append("\"extra_").append(i).append("\":\"extra_value_").append(i).append("\"");
      if (i < extraFields - 1) {
        json.append(",");
      }
    }
    json.append("}");
    return json.toString();
  }


  public static List<List<String>> generateAllValueCombos(int comboCount) {
    List<List<String>> allCombinations = new ArrayList<>();

    int totalCombos = 1 << comboCount;

    for (int i = 0; i < totalCombos; i++) {
      List<String> currentCombination = new ArrayList<>(comboCount);
      for (int j = comboCount - 1; j >= 0; j--) {
        if (((i >> j) & 1) == 0) {
          currentCombination.add("value-" + j);
        } else {
          currentCombination.add(null);
        }
      }
      allCombinations.add(currentCombination);
    }

    return allCombinations;
  }

}
