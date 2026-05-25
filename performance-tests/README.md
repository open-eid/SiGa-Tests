# SiGa performance tests (Gatling)

Java-DSL Gatling load tests for the three SiGa signing happy flows: **MID** (Mobile-ID, async polling), **SID** (Smart-ID, two-phase async polling), and **ID-Card** (synchronous remote signing).

This is a self-contained Maven project under `gatling/` and does **not** affect the existing integration-test build (`./mvnw verify` is untouched).

## Prerequisites

- **Java 21** (`JAVA_HOME` must point at a JDK 21 install — the parent integration-test repo also requires 21)
- **Maven 3.8+**
- A reachable SiGa instance — local docker-compose at `https://localhost:8443`, dev/staging env, or any deployment with valid HMAC client credentials
- Optional: MID/SID mocks configured **at SiGa's upstream** so the load generator only ever talks to SiGa. Real demo MID/SID providers will rate-limit under load and become the bottleneck.

The test signing certificate (`sign_ECC_from_TEST_of_ESTEID2018.p12`, password `1234`) is bundled in `src/test/resources/` and used for the ID-Card / remote-signing flow.

## Simulations

| Class | Flow |
|---|---|
| `ee.openeid.siga.perf.simulation.MidSimulation` | Create container → start MID → poll status until terminal |
| `ee.openeid.siga.perf.simulation.SidSimulation` | Create → certificate-choice → poll → start SID → poll |
| `ee.openeid.siga.perf.simulation.RemoteSigningSimulation` | Create → start remote → sign locally with P12 → finalize |
| `ee.openeid.siga.perf.simulation.MixedSimulation` | Create container, then a weighted random pick of the three flows |

All four use **datafile containers** (small inline file in the create body).

## Running

From this directory (`gatling/`):

```bash
# MID only
mvn gatling:test -Dgatling.simulationClass=ee.openeid.siga.perf.simulation.MidSimulation

# SID only
mvn gatling:test -Dgatling.simulationClass=ee.openeid.siga.perf.simulation.SidSimulation

# ID-Card (remote signing) only
mvn gatling:test -Dgatling.simulationClass=ee.openeid.siga.perf.simulation.RemoteSigningSimulation

# Random mix of all three
mvn gatling:test -Dgatling.simulationClass=ee.openeid.siga.perf.simulation.MixedSimulation
```

`JAVA_HOME` must be set to JDK 21, e.g. `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn ...`.

Reports are written to `target/gatling/<simulation-name>-<timestamp>/index.html`.

## Configuration

Everything is a `-D` system property. Defaults match the local SiGa docker-compose stack and `SERVICE_UUID_1` / `SERVICE_SECRET_1` from the parent integration tests.

### Target environment

| Property | Default | Meaning |
|---|---|---|
| `siga.protocol` | `https` | URL scheme |
| `siga.host` | `localhost` | hostname |
| `siga.port` | `8443` | port |
| `siga.contextPath` | *(empty)* | optional context path prefix |

### HMAC client credentials

| Property | Default | Meaning |
|---|---|---|
| `siga.serviceUuid` | `a7fd7728-a3ea-4975-bfab-f240a67e894f` | client UUID |
| `siga.serviceSecret` | `746573745365637265744b6579303031` | client secret |
| `siga.hmacAlgorithm` | `HmacSHA256` | HMAC algorithm |

For non-local environments, supply real credentials issued for that env (see https://github.com/open-eid/SiGa/wiki/Authorization).

### Test identities

| Property | Default | Meaning |
|---|---|---|
| `siga.midPersonId` | `60001019906` | MID demo personal code |
| `siga.midPhone` | `+37200000766` | MID demo phone |
| `siga.sidPersonId` | `50001029996` | SID demo personal code |
| `siga.sidCountry` | `EE` | SID country code |

### Load profile

Two injection modes — pick one with `-Dmode=ramp` (default) or `-Dmode=constant`.

**Ramp mode** (`-Dmode=ramp`, default) — single-shot: inject `users` total virtual users evenly over `ramp` seconds, each user runs the scenario once. Use this to push a fixed number of flows.

| Property | Default | Meaning |
|---|---|---|
| `users` | `10` | total virtual users (= total signature flows) |
| `ramp` | `10` | ramp-up duration in seconds |

**Constant-rate mode** (`-Dmode=constant`) — sustained throughput: inject `flowsPerMinute / 60` users per second for `durationSeconds`, each user runs the scenario once. Use this for steady-state load.

| Property | Default | Meaning |
|---|---|---|
| `flowsPerMinute` | `60` | target rate of new signature flows per minute (60 ≈ 1/s) |
| `durationSeconds` | `60` | how long to sustain the constant rate |
| `warmupSeconds` | `0` | optional ramp from 0 to target rate before the constant phase |

Note: each user runs the scenario **once**. The injection rate is the rate at which new flows _start_; the rate at which flows _complete_ trails by the flow's wall-clock duration (≈ 1 s for ID-Card, ≈ poll-time for MID/SID).

### Polling (MID/SID only)

| Property | Default | Meaning |
|---|---|---|
| `pollIntervalMs` | `3500` | wait between MID/SID status polls |
| `pollTimeoutMs` | `300000` | max wait for terminal MID/SID status (caps poll iterations) |

### Mixed simulation weights

| Property | Default | Meaning |
|---|---|---|
| `weights.mid` | `33.0` | relative MID weight |
| `weights.sid` | `33.0` | relative SID weight |
| `weights.remote` | `34.0` | relative ID-Card weight |

Weights are normalised internally; absolute values don't matter, only their ratios.

## Examples

**Higher load on the random mix, against local SiGa:**

```bash
mvn gatling:test \
    -Dgatling.simulationClass=ee.openeid.siga.perf.simulation.MixedSimulation \
    -Dusers=200 -Dramp=60
```

**Sustained 100 signature flows per minute for 30 minutes, with a 30 s warmup:**

```bash
mvn gatling:test \
    -Dgatling.simulationClass=ee.openeid.siga.perf.simulation.RemoteSigningSimulation \
    -Dmode=constant -DflowsPerMinute=100 -DdurationSeconds=1800 -DwarmupSeconds=30
```

**ID-Card only against a remote dev environment:**

```bash
mvn gatling:test \
    -Dgatling.simulationClass=ee.openeid.siga.perf.simulation.RemoteSigningSimulation \
    -Dsiga.host=siga-dev.example.com -Dsiga.port=443 \
    -Dsiga.serviceUuid=YOUR-UUID -Dsiga.serviceSecret=YOUR-SECRET \
    -Dusers=100 -Dramp=30
```

**Mix skewed towards ID-Card (60%) with shorter MID/SID polling for a fast mock:**

```bash
mvn gatling:test \
    -Dgatling.simulationClass=ee.openeid.siga.perf.simulation.MixedSimulation \
    -Dweights.mid=20 -Dweights.sid=20 -Dweights.remote=60 \
    -DpollIntervalMs=500 -DpollTimeoutMs=5000
```

## Unit tests

A small JUnit 5 suite verifies the ports of the parent project's HMAC and ECDSA helpers:

```bash
mvn test
```

- `SigaHmacTest` — HMAC algorithm correctness, signable-string format (matches `RequestBuilder.signRequest`), URL-encoding rules, full header set
- `DigestSignerTest` — ECDSA signature produced by `DigestSigner` verifies against the keystore's own certificate; signing certificate base64 is well-formed

These run in seconds and do **not** require a SiGa instance.

## Notes

- Each request computes its HMAC headers per call (timestamp + signature regenerated). Do not introduce caching across requests — SiGa rejects requests whose `X-Authorization-Timestamp` is too far from server time.
- Polling for MID/SID is non-blocking via Gatling's `asLongAs` + `pause`. Each poll loop is bounded by `pollTimeoutMs / pollIntervalMs` iterations to prevent runaway scenarios when a status never reaches a terminal state.
- The ID-Card flow uses `Signature.getInstance("NONEwithECDSA")` directly over the bundled EC P12 — no `digidoc4j` dependency. Real ID cards cannot be driven from a load generator anyway; a soft P12 is the standard load-test pattern.
- The MID and SID demo providers cannot sustain meaningful concurrent load. For SiGa-focused throughput numbers, point SiGa at internal mocks of those services.
