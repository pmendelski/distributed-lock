description = "Sherlock Distributed Lock in-memory implementation"

dependencies {
    api(project(":api:api-sync"))
    api(project(":inmem:inmem-common"))
    integrationImplementation(project(":tests"))
}
