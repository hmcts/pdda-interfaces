#!groovy

@Library("Infrastructure")

def type = "java"
def product = "pdda"
def component = "pdda"

withPipeline(type, product, component) {
    enableDbMigration(product)
}

static LinkedHashMap<String, Object> secret(String secretName, String envVar) {
    [ $class: 'AzureKeyVaultSecret',
        secretType: 'Secret',
        name: secretName,
        version: '',
        envVariable: envVar
    ]
}

