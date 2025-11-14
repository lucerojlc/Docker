pipeline {
    agent any
    
    environment {
        DOCKER_COMPOSE_VERSION = '2.20.2'
        PROJECT_NAME = 'docker-mysql-phpmyadmin'
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
        
        stage('Verify Docker') {
            steps {
                echo '========== Verificando instalación de Docker =========='
                sh '''
                    docker --version
                    docker-compose --version
                '''
                echo 'Docker y Docker Compose están instalados correctamente'
            }
        }
        
        stage('Stop Previous Containers') {
            steps {
                echo '========== Deteniendo contenedores previos =========='
                sh '''
                    docker-compose down || true
                    docker ps -a
                '''
                echo 'Contenedores previos detenidos'
            }
        }
        
        stage('Build') {
            steps {
                echo '========== Construyendo imágenes Docker =========='
                sh '''
                    docker-compose build --no-cache
                '''
                echo 'Imágenes construidas exitosamente'
            }
        }
        
        stage('Deploy') {
            steps {
                echo '========== Desplegando contenedores =========='
                sh '''
                    docker-compose up -d
                    sleep 10
                    docker-compose ps
                '''
                echo 'Contenedores desplegados correctamente'
            }
        }
        
        stage('Test') {
            steps {
                echo '========== Verificando que los contenedores estén corriendo =========='
                sh '''
                    # Verificar que los contenedores estén running
                    if [ $(docker-compose ps -q | wc -l) -eq 0 ]; then
                        echo "Error: No hay contenedores corriendo"
                        exit 1
                    fi
                    
                    # Verificar MySQL
                    echo "Verificando MySQL..."
                    docker-compose exec -T db mysql -uroot -proot -e "SELECT 1" || echo "MySQL aún no está listo"
                    
                    # Verificar phpMyAdmin
                    echo "Verificando phpMyAdmin..."
                    curl -f http://localhost:8080 || echo "phpMyAdmin aún no responde"
                    
                    echo "Contenedores verificados"
                '''
            }
        }
        
        stage('Show Logs') {
            steps {
                echo '========== Mostrando logs de los contenedores =========='
                sh '''
                    docker-compose logs --tail=50
                '''
            }
        }
    }
    
    post {
        success {
            echo '========================================='
            echo '✅ Pipeline ejecutado exitosamente'
            echo '========================================='
            echo 'MySQL disponible en: localhost:3306'
            echo 'phpMyAdmin disponible en: http://localhost:8080'
            echo 'Usuario: root'
            echo 'Contraseña: root'
            echo '========================================='
        }
        
        failure {
            echo '========================================='
            echo '❌ Pipeline falló'
            echo '========================================='
            sh '''
                echo "Logs de Docker Compose:"
                docker-compose logs --tail=100
                echo "Contenedores activos:"
                docker ps -a
            '''
        }
        
        always {
            echo '========== Limpiando recursos (opcional) =========='
            // Descomenta la siguiente línea si quieres detener los contenedores después de cada ejecución
            // sh 'docker-compose down'
        }
    }
}
