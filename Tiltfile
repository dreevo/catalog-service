# Build
custom_build(
     # Name of the container image
     ref = 'catalog-service',
     tag = 'latest',
     # Command to build the container image
     command = '.\\gradlew.bat bootBuildImage --imageName catalog-service:latest',
     # Files to watch that trigger a new build
     deps = ['build.gradle', 'src']
)
# Deploy
#k8s_yaml(['k8s\\deployment.yml', 'k8s\\service.yml'])
k8s_yaml(kustomize('k8s'))
# Manage
k8s_resource('catalog-service', port_forwards=['9001'])