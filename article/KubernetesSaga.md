## По следам Kubernetes

Команда Amplicode в рамках Deployment tools подобралась к Kubernetes.

Обсуждая, а что собственно можно дать разработчику в этой части, мы пришли к вопросам:

- делают ли разработчики Kubernetes
- есть ли жизнь в Kubernetes без Helm
- есть ли жизнь в Kubernetes без Operator (for Postgres example)

И если на первый вопрос ответ в целом найти не сложно, пошли на HH, погуглили вакансии,
кажется делают, то с остальными все не так однозначно. I need help :-)

### Есть ли жизнь в Kubernetes без Helm

Идем в Google и ищем, а что собственно нам предлагают по запросу Kubernetes + Spring Boot. Находим
несколько статей. Часа 2 внимательного чтения и кажется что все пишут исключительно Kubernetes
Manifest файлы. Но все примеры в статьях ограничиваются чем-то простым: Deployment, Service,
иногда ServiceAccount. А как же остальные типы ресурсов: Role, RoleBinding, Ingress.

Смотрим по сторонам и ищем тех, кто что-то по настоящему деплоит в Kubernetes. И слышим от них,
что мол юзай Helm и будет тебе счастье. Возвращаемся в Google, а что там у нас дальне на странице
и тут уже появляются статьи, которые рассказывают о совместном использовании Helm + Kubernetes.

**Вопрос к читателю:** А как у вас, как вы деплоити ваши приложения в Kubernetes?

#### Пробуем освоить Helm не зная Helm, и возможно даже Kubernetes

Есть амбициозная задача - задеплоить Spring Boot приложение в Kubernetes и есть PetClinic. И
несмотря на то, что сварщики мы не настоящие деплоить будем по взрослому:
- PetClinic (Spring Boot App) 
- PostgreSQL
- PgAdmin
- Kafka

Руководство нам завещает начинать с `helm create NAME` не будем отступать. Создаем в нашем
приложение папку kuber, внутри выполняем:

`$ helm create petclinic`

![](/Users/ikuchmin/Sources/spring-projects/spring-petclinic/article/helm_init_tree.png)

Helm нам сгенерил шаблон с множеством файлов. Идем в Helm документацию разберемся что за файлы:

- все что в `templates/*` это база для генерации, шаблоны на Go template (выглядит не привлекательно)
- Chart.yaml содержит описание нашего проекта (некоторый аналог build.gradle)
- values.yaml содержит параметры описывающие наш проект

Что-то многовато файлов. Разрабатывая Docker Compose мы разбили задачу на два шага:
1. Поддержка Dev Environment для локального девелопмента
2. Поддержка деплоя приложения + environment

##### Dev Environment в Kubernetes

Пробуем пройти той же дорожкой, но уже для Kubernetes + Helm. Удаляем все что у нас в templates,
и пробуем (предварительно перейдя в директорию нашего Chart):

`$ helm install petclinic .`

Все задеплоилось корректно, возвращаемся в доку. Идем в [Quickstart Guide](https://helm.sh/docs/intro/quickstart/),
нам там предлагают, пробуем:

```
$ helm repo add bitnami https://charts.bitnami.com/bitnami
$ helm install bitnami/mysql --generate-name
```

Это все конечно замечательно, но мы то хотим все по взрослому, чтобы `helm install` для нашего
приложения сделал и все разом поднялось. Каждый Линуксоид знает раз есть пакет, значит есть и зависимости.
Возвращаемся в Google и первая же ссылка [Helm Dependency](https://helm.sh/docs/helm/helm_dependency/). Пробуем.

_Тут бы нам мог помочь Amplicode, но увы он пока не умеет добавлять пакеты Helm_,
поэтому в Google (или ChatGPT/Copilot) `postgresql dependency helm` и первая же статья
нам предлагает:

```yaml
dependencies:
  - name: postgresql
    version: "10.13.8"
    repository: https://charts.bitnami.com/bitnami
    condition: postgresql.enabled
```

Какой-то тут старый PostgreSQL, какой там у нас свежий - 16.2, правим версию в зависимости
и выполняем `$ helm upgrade petclinic .`, получаем ошибку, что нужно обновить зависимости,
выполняем:

```
$ helm dependency update
```

Получаем ошибку `Error: can't get a valid version for repositories postgresql. Try changing the version constraint in Chart.yaml`.
Первое предположение, что у Bitnami образа, есть минорная версия. Идем на страницу пакета в [ArtifactHub Bitnami PostgreSQL](https://artifacthub.io/packages/helm/bitnami/postgresql).

Видим, что последняя версия: 14.2.4. Неужели такая большая задержка между Helm пакетом и реальной версией приложения?
Нам сейчас это конечно не важно, но в реальном приложении хотелось бы чего по свежее. Меняем версию в Chart.yaml
обновляем зависимости и пакет нашего приложения.

```
$ helm dependency update
$ helm upgrade petclinic .
```

Смотрим в логи запущенного пода `kubectl logs petclinic-postgresql-0` и обнаруживаем для себя,
что `LOG:  starting PostgreSQL 16.2`. Значит версия Helm пакета не соответствует версии приложения.
_Жаль конечно, что Amplicode не умеет показывать версию приложения в пакете Helm._

PostgreSQL то у нас стартовал, но:
- в нем нет соответствующего пользователя и DB
- у нас нет доступа с локального компьютера

_То что Amplicode для Docker Compose делает из коробки, для Helm нам придется решить руками_,
возвращаемся на страницу описания пакета [ArtifactHub Bitnami PostgreSQL](https://artifacthub.io/packages/helm/bitnami/postgresql). Внимательнейшим образом
читаем страницу с параметрами (там их не один десяток), а под ней еще и документация:

![](/Users/ikuchmin/Sources/spring-projects/spring-petclinic/article/helm_postgresql_param.png)

Изучив документацию, правим `values.yaml` (предварительно полностью
его почистив), добавляем в пустой файл:

```yaml
postgresql:
  enabled: true
  auth:
    username: root
    password: root
    database: spring-petclinic
```

По неизвестной причине `helm upgrade ...` не работает, поэтому переустанавливаем наш Chart:

```
$ helm uninstall petclinic
$ helm install petclinic .
```

Далее необходимо получить доступ к запущенному PostgreSQL с локального компьютера. Идем снова в гугл
или ChatGPT/Copilot, случайно оказываемся на YouTube, смотрим несколько видосиков про кошечек или ...
и где-то через минут 30 вспоминаем, что мы тут приложение кубернизируемо, закрываем YouTube. На этот раз
идем в доку Kubernetes и узнаем о существовании [kubectl proxy](https://kubernetes.io/docs/concepts/cluster-administration/proxies/),
NodePort, LoadBalancer, Ingress. Осталось выбрать тот что подходит под нашу задачу. Вспоминаем
что мы хотим: "Развернуть Dev Environment в Kubernetes".

В процессе чтения доки выясняем, что:
- kubectl proxy позволяет доступиться только по HTTP/HTTPS сервисам
- NodePort и LoadBalancer на dev environment в режиме одной ноды работают идентично
- Ingress требует дополнительно установки Ingress Controller, мы уверены что нам это нужно?

Кажется, что в нашей текущей ситуации нам было бы полезно узнать только про NodePort и LoadBalancer
и таки уже начать править values.yaml или может уже пришло время писать свой Chart template? И тут
закрадывается мысль, помниться в Bitnami для PostgreSQL была таблица с огромным количеством парамтреов,
давайте посмотрим на нее еще раз на предмет NodePort и LoadBalancer.

![](/Users/ikuchmin/Sources/spring-projects/spring-petclinic/article/helm_postgresql_type.png)

Выясняется, что образы Bitnami поддерживают возможность определения service type через параметры.
Отлично, открываем values.yaml и правим нашу конфигурацию PostgreSQL:

```yaml
postgresql:
  enabled: true
  auth:
    username: root
    password: root
    database: spring-petclinic
  primary:
    service:
      type: LoadBalancer
      nodePorts:
        postgresql: 30543
```

Тип использован LoadBalancer поскольку он работает стабильнее в разных конфигурациях.
Порт указан статически, чтобы при reinstall он не менялся.

Выполняем команды для переустановки нашего пакета:

```yaml
$ helm uninstall petclinic
$ helm install petclinic .
```

Получаем external IP для нашего PostgreSQL:

```yaml
$ kubectl get service petclinic-postgresql -n default -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
```

Теперь зная ip и порт(30543) правим конфигурацию dataSource в PetClinic (например используя Amplicode Explorer)
и запускаем приложение.

**Вопрос к читателю:** Давайте представим, что в Amplicode есть возможность добавлять сервисы используя Helm пакеты
в качестве зависимостей. Это могло бы выглядеть следующим образом. Открываем Amplicode Designer
/вызываем окно Generate(Cmd+N/Alt+Ins) вызываем действие добавление сервиса PostgreSQL. Amplicode
показывает окно в котором отображается название пакета, его версия, версия приложения, а также
заполнены поля db, user, password (предварительно зачитанные из Spring Boot Datasource), а также
есть возможность указать, что необходимо дать доступ к сервису из вне Kubernetes. Автоматически
подобран порт, по умолчанию выбран LoadBalancer, но есть возможность выбрать NodePort.
А после завершения диалога, Amplicode добавляет в наш Chart зависимость и создает определенную
в диалоге конфигурацию. После чего мы можем сразу же перейти к установке нашего пакета.

Используя Amplicode с поддержкой Helm можно было бы добавить например кластер MongoDB, Kafka, Redis    

_**Как вы считаете, насколько такая функицональность была бы полезна?**_
