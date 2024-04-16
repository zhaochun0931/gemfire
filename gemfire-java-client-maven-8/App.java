// Create a Geode cache instance
        Cache cache = new CacheFactory().create();

        // Create a region using RegionFactory
        RegionFactory<String, String> regionFactory = cache.createRegionFactory(RegionShortcut.REPLICATE);
        Region<String, String> region = regionFactory.create("exampleRegion");

        // Print a message to confirm region creation
        System.out.println("Region 'exampleRegion' created successfully.");

        // Close the cache
        cache.close();
