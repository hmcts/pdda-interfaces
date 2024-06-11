#!groovy

@Library("Infrastructure")

def type = "java"
def product = "pdda"
def component = "public-display-data-aggregator"
def secrets = [
  'pdda-${env}': [
    secret('public-display-data-aggregator-POSTGRES-USER', 'DB_USER_NAME'),
    secret('public-display-data-aggregator-POSTGRES-PASS', 'DB_PASSWORD'),
    secret('public-display-data-aggregator-POSTGRES-HOST', 'DB_HOST'),
    secret('public-display-data-aggregator-POSTGRES-PORT', 'DB_PORT'),
    secret('public-display-data-aggregator-POSTGRES-SCHEMA', 'DB_SCHEMA')
  ],
]

withPipeline(type, product, component) {
  enableDbMigration(product)
  loadVaultSecrets(secrets)

  // Vars for Kubernetes
  env.TEST_URL = "https://pdda-public-display-data-aggregator-staging.staging.platform.hmcts.net"
  env.DB_NAME = "pdda"

  afterAlways('smokeTest:stg') {
    steps.archiveArtifacts allowEmptyArchive: true, artifacts: 'smoke-test-report/**/*'
  }
}

static LinkedHashMap<String, Object> secret(String secretName, String envVar) {
    [ $class: 'AzureKeyVaultSecret',
        secretType: 'Secret',
        name: secretName,
        version: '',
        envVariable: envVar
    ]
}

