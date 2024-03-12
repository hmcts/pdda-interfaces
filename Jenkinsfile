#!groovy

@Library("Infrastructure")

def type = "java"
def product = "pdda"
def component = "public-display-data-aggregator"

withPipeline(type, product, component) {
    enableDbMigration(product)
    loadVaultSecrets(secrets)
}

static LinkedHashMap<String, Object> secret(String secretName, String envVar) {
    [ $class: 'AzureKeyVaultSecret',
        secretType: 'Secret',
        name: secretName,
        version: '',
        envVariable: envVar
    ]
}

