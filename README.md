# SB Video Converter
Conversor de vídeos para formatos compatíveis com os padrões web.\
[SB Video Converter](http://ec2-54-242-95-215.compute-1.amazonaws.com/)

## Linguagens e Ferramentas
- Java 1.8
- Spring Boot 2.0.4 
- Gradle 4.10
- Tomcat 8
- Zencoder API
- Material Design Lite


## Variáveis de Ambiente
As variáveis de ambiente abaixo foram utilizadas para manter a segurança e organização das chaves privadas e variáveis voláteis do projeto.
Portanto, devem ser definidas para o correto funcionamento do sistema.  

#### Amazon S3 
```bash
S3_ACCESS_KEY  
S3_SECRET_KEY
S3_REGION
S3_BUCKET
S3_ENDPOINT_URL
```

#### Zencoder
```bash
ZENCODER_API_KEY
ZENCODER_ENDPOINT_URL
```

#### Spring
```bash
MAX_FILE_SIZE
MAX_REQUEST_SIZE
```

## Fluxo de Desenvolvimento
- Antes de começar o desenvolvimento, clone o projeto e crie um novo branch.
- Crie uma issue detalhando o que será implementado, bem como o label referente à ela.
- Após o término do desenvolvimento, crie um Pull Request e solicite a revisão do código.
- Após aprovado, execute o merge para o master e gere uma nova versão.

## Gradle
Instale o Gradle para executar os testes unitários e realizar o build do arquivo **.war** para a implantação me produção.
[Clique aqui](https://gradle.org/install/) para acessar o tutorial de instalação.

## Configuração do  Servidor
##### Atualize os pacotes do YUM 
```
sudo su
yum list installed
yum update
```

##### Instale o Java 8
```
yum install java-1.8.0
yum remove java-1.7.0-openjdk
```

##### Instale Tomcat 8.x
```
yum install tomcat8 tomcat8-webapps tomcat8-admin-webapps tomcat8-docs-webapp
```

##### Iniciar o serviço do Tomcat
```
service tomcat8 start
```

##### Configuração do Tomcat
* Edite o arquivo de usuários do Tomcat
```
cd  /usr/share/tomcat8
vim /usr/share/tomcat8/conf/tomcat-users.xml
```
* Adicione a linha abaixo inserindo a senha para o acesso às funcionalidades de administração do TomCat
```
<user name="USERNAME" password="PASSWORD" roles="admin,manager,admin-gui,admin-script,manager-gui,manager-script,manager-jmx,manager-status" />
```

##### Confira a instalação do Tomcat
* netstat: Lista todos os sockets da rede escutando na porta 8080 
```
netstat -na | grep 8080
```

##### Defina o Auto Start para o serviço do Tomcat
```
sudo chkconfig --list tomcat8
sudo chkconfig tomcat8 on
```

##### Instale o Nginx
```
yum install nginx
```

##### Adicione o domínio ao Nginx
```
vi /etc/nginx/conf.d/sbvideoconverter.conf
```

```
server {
    listen       80;
    listen       [::]:80;
    server_name  ec2-54-242-95-215.compute-1.amazonaws.com;
    root         /usr/share/nginx/html;
	location / {
    	proxy_connect_timeout 300;
        proxy_send_timeout 300;
        proxy_read_timeout 300;
        proxy_pass http://localhost:8080;
        }
}
```

##### Inicie o Nginx
```
service nginx start
```
##### Defina o Auto Start para o serviço do Nginx
```
sudo chkconfig --list nginx
sudo chkconfig nginx on
```

## Testes Unitários
Para executar os testes unitários, execute a task test do Gradle
 ```bash
gradle test
 ```
 
## Deploy
- Execute a task build do Gradle, a qual irá compilar a aplicação, rodar os testes unitários e gerar o arquivo **.war**. 
 ```bash
gradle build
 ```
 
- Após a execução, o gradle criará a pasta build no diretório raiz do projeto. O arquivo **.war** estará no caminho a seguir:
```bash
sb-videoconverter/build/libs/sb-ideoconverter-[VERSION].war
```
  
- Altere o nome do arquivo para **ROOT.war**.

- A seguir, acesse o painel de gerenciamento do TomCat acessando:
\
*http://ec2-54-242-95-215.compute-1.amazonaws.com/manager*

- Será solicitado o nome de usuário admistrador e senha, definidos na instalação do TomCat.

- Após o login, clique em undeploy da aplicação que esteja sendo executada na raiz do domínio.

- Clique em escolher arquivo, selecione o arquivo **ROOT.war** e clique em deploy. A partir daí, a aplicação será implantada e estará disponível em produção.