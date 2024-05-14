#!groovy

@Library("Infrastructure")

def type = "java"
def product = "pdda"
def component = "public-display-data-aggregator"

withPipeline(type, product, component) {
  enableDbMigration(product)

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

