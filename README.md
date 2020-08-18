# Sample Metabase Driver: redis

![screenshot](screenshots/redis-driver.png)

All you need you do is drop the driver in your `plugins/` directory. You can grab it [here](https://github.com/metabase/redis-driver/releases/download/1.0.0/redis.metabase-driver.jar) or build it yourself:

## Building the driver

### Prereq: Install Metabase as a local maven dependency, compiled for building drivers

Clone the [Metabase repo](https://github.com/metabase/metabase) first if you haven't already done so.

```bash
cd /path/to/metabase_source
lein install-for-building-drivers
```

### Build the redis driver

```bash
# (In the redis driver directory)
lein clean
DEBUG=1 LEIN_SNAPSHOTS_IN_RELEASE=true lein uberjar
```

### Copy it to your plugins dir and restart Metabase

```bash
mkdir -p /path/to/metabase/plugins/
cp target/uberjar/redis.metabase-driver.jar /path/to/metabase/plugins/
jar -jar /path/to/metabase/metabase.jar
```

_or:_

```bash
mkdir -p /path/to/metabase_source/plugins
cp target/uberjar/redis.metabase-driver.jar /path/to/metabase_source/plugins/
cd /path/to/metabase_source
lein run
```
