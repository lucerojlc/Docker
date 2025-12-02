pipeline {
    agent any
    
    environment {
        DOCKER_COMPOSE_VERSION = '2.20.2'
        PROJECT_NAME = 'docker-mysql-phpmyadmin'
        WORK_DIR = 'contenedor1'
        MAVEN_HOME = tool 'Maven'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '========== Clonando repositorio desde GitHub =========='
                git branch: 'main', 
                    url: 'https://github.com/lucerojlc/Docker.git'
                echo 'Repositorio clonado exitosamente'
            }
        }
        
        stage('Verify Tools') {
            steps {
                echo '========== Verificando herramientas instaladas =========='
                sh '''
                    mvn --version
                    java -version
                '''
                echo 'Maven y Java están instalados correctamente'
            }
        }
        
        stage('Build Application') {
            steps {
                echo '========== Compilando aplicación Java =========='
                sh '''
                    mvn clean compile
                '''
                echo 'Aplicación compilada exitosamente'
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo '========== Ejecutando pruebas unitarias =========='
                sh '''
                    mvn test
                '''
                echo 'Pruebas unitarias completadas'
            }
            post {
                always {
                    // Publicar resultados de pruebas unitarias
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Code Coverage') {
            steps {
                echo '========== Generando reporte de cobertura de código =========='
                sh '''
                    mvn jacoco:report
                '''
                echo 'Reporte de cobertura generado'
            }
            post {
                always {
                    // Publicar reporte de cobertura JaCoCo
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/*Test*.class'
                    )
                    
                    // Publicar reporte HTML
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }
        
        stage('Code Quality Check') {
            steps {
                echo '========== Verificando calidad de código =========='
                sh '''
                    mvn jacoco:check || echo "Advertencia: Cobertura por debajo del umbral"
                '''
            }
        }
        
        stage('Generate Test Reports') {
            steps {
                echo '========== Generando reportes consolidados =========='
                sh '''
                    mvn surefire-report:report || true
                '''
            }
            post {
                always {
                    // Publicar reporte de pruebas
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site',
                        reportFiles: 'surefire-report.html',
                        reportName: 'Test Results Report'
                    ])
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                echo '========== Ejecutando pruebas de integración =========='
                sh '''
                    echo "Pruebas de integración requieren Docker configurado"
                    echo "Se ejecutarán cuando se configure Docker correctamente"
                    # mvn verify -DskipUnitTests=true
                '''
            }
        }
    }
    
    post {
        success {
            echo '========================================='
            echo '✅ Pipeline de Testing ejecutado exitosamente'
            echo '========================================='
            echo '📊 Reportes disponibles:'
            echo '   - JaCoCo Coverage Report'
            echo '   - Test Results Report'
            echo '   - Unit Test Results'
            echo '========================================='
        }
        
        failure {
            echo '========================================='
            echo '❌ Pipeline falló'
            echo '========================================='
            echo 'Revisar logs para más detalles'
        }
        
        unstable {
            echo '========================================='
            echo '⚠️  Build inestable - Revisar pruebas'
            echo '========================================='
        }
        
        always {
            echo '========== Limpieza final =========='
            
            // Archivar artefactos importantes
            archiveArtifacts artifacts: '**/target/*.jar, **/target/site/**/*', 
                             allowEmptyArchive: true
            
            // Guardar reportes de cobertura
            archiveArtifacts artifacts: '**/target/site/jacoco/**/*', 
                             allowEmptyArchive: true
            
            echo 'Pipeline finalizado'
        }
    }
}
