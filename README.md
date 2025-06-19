# Portfolio: Kevyt projektinhallintamalli käyttäen Event Sourcing(ES) ja Axon-frameworkia

Tämä on esimerkki kevytprojektinhallintamallista, jossa tiimit ja projektit ja hyväksyjät toimivat domain-aggregaatteina. Projektin tavoitteena on havainnollistaa domain-keskeistä arkkitehtuuria, jossa liiketoimintasäännöt asuvat aggregaateissa, ei serviceissä. 


## Tavoite

- Demonstroida Domain-Driven Design (DDD) -pohjaista arkkitehtuuria yhdistettynä CQRS:ään (Command Query Responsibility Segregation) ja Event Sourcingiin.
- Korostaa liiketoimintalogiikan sijoittamista aggregaatteihin palvelukerroksen sijaan.
- Hyödyntää Axon Frameworkin kyvykkyyksiä tapahtumien hallintaan ja pitkien prosessien orkestrointiin (Saga Pattern).


## Teknologiat

- Java 17
- Maven
- PostgreSQL
- Docker
- Axon
- Spring Boot
- Spring JPA
- JUnit 5 + Mockito

## Peruskäyttö

Sovellus vaatii PostgreSQL:n, joka ajetaan Dockerin kautta. docker-compose.yml on konfiguroitu seuraavasti:

### .env-tiedosto (luo juureen kopioimalla .env-example ja muokkaamalla)
POSTGRES_PASSWORD=salasana123

PGDATA_VOLUME=/c/Users/demo/postgres-data

#### PostgreSql-kontin käynnistys
```docker compose up -d```

### Spring-boot 
Sovellus olettaa ympäristömuuttujista löytyvän tietokannan salasanan. Sen voi Windowsin cmd-promptissa asettaa esimerkiksi näin ```set POSTGRES_PASSWORD=salasana123```
Itse sovellus käynnistetään ajamalla komento ```mvn spring-boot:run```

## Domainin rakenne

- **Project**: Omistaa alkuarvion (InitialEstimation), projektille lisätään tehtäviä. Projekti itse huolehtii tehtäviä lisättäessä, että arvioitu aika-arvio ei ylity. Projektille lisätään myös sen luomisen yhteydessä ns. hyväksyjät
- **Team**: Omistaa tiimin jäsenet ja vastaa tehtävien hallinnasta.
- **ProjectTask**: On osa projektisuunnittelua. Se määrittää mitä pitää tehdä ja kuinka paljon työtä siihen on alun perin arvioitu.
- **TeamTask**: Edustaa sitä, miten tiimi toteuttaa projektitehtävän: kuka tekee sen, missä vaiheessa se on, ja paljonko todellista aikaa kului.
- **Approval**: Kun projektin kaikki tehtävät ovat valmistuneet, lähtee sähköpostia projektin hyväksyjille, heidän vastuullaan on käydä joko hyväksymässä tai hylkäämässä projekti. (Tämä demonstroi ns. long running prosessia)

## Value Objectit

- **TimeEstimation**: Abstraktoi ajan arvion. Estää virheelliset arvot (esim. negatiiviset tunnit).
- **ActualSpentTime**: Kuvaa oikeasti kulunutta aikaa. Voi päivittyä vasta kun task on valmis.
- **ProjectId, ProjectTaskId, TaskId, TeamId, TeamTaskId, TeamMemberId, ApprovalId, ApproverId**: Varmistavat oikeat ID-käytännöt ilman paljaita merkkijonoja tai UUID:itä.


## CQRS ja Event Sourcing
Projekti käyttää Axon Frameworkia CQRS:n ja Event Sourcingin toteuttamiseen:

- Komentomalli (Write Side): Komennot, kuten CreateProjectCommand ja AddTaskToProjectCommand, käynnistävät liiketoimintalogiikan aggregaateissa (ProjectAggregate, TeamAggregate). Tapahtumat, kuten ProjectCreatedEvent ja TaskAddedToProjectEvent, tallennetaan Axon Serverin tapahtumavarastoon.
- Kyselymalli (Read Side): Projektiot, kuten ProjectProjection, päivittävät lukumallin tapahtumien perusteella. 
- ProjectApprovalSaga käynnistyy kun projekti luodaan. Projektin jokainen hyväksyjä lisätään ProjectApprovalSaga:n sisäiseen listaan tarvittavista hyväksyjistä. Kun joku hyväksyjistä hylkää projektin, saga päättyy siihen ja projekti merkitään "REJECTED". Kun kaikki hyväksyjät ovat hyväksyneet, projekti merkitään "APPROVED" ja saga päättyy


## REST-endpointit (esimerkit)

### Luo projekti
```curl --location 'http://localhost:8080/projects' --header 'Content-Type: application/json' --data-raw '{"name":"ddd portfolio", "description":"using axon", "estimatedEndDate": "2026-01-01", "estimation":{"hours":10,"minutes":55}, "projectApprovers":[{"name":"teppo testaaja", "email":"teppo.foo@bar.fi", "role":"QA"}, {"name":"tapio niemelä", "email":"tapio.foo@bar.com", "role":"PROJECT_MANAGER"}]}''```

### Lisää taski projektille
```curl --location 'http://localhost:8080/projects/cd8a4243-717b-4181-bb5a-83381f511920/tasks' --header 'Content-Type: application/json' --data '{"name":"java code", "description":"make java code demonstrating ddd and spring data jdbc", "estimation":{"hours":8, "minutes":0}}'```
### Lisää tiimi
```curl --location 'http://localhost:8080/teams' --header 'Content-Type: application/json' --data '{"name":"ddd and spring data jdbc demonstration team"}'```

### Lisää tiimille jäsen
```curl --location 'http://localhost:8080/teams/791031a6-922b-4ea0-93da-ae7b21a7a09b/members' --header 'Content-Type: application/json' --data '{"name":"tapio niemelä", "profession":"ddd enthuistic"}'```

### Poista jäsen tiimistä
```curl --location --request DELETE 'http://localhost:8080/teams/791031a6-922b-4ea0-93da-ae7b21a7a09b/members/c41c9a87-688f-428d-a1d5-4134f1faeeaf'```

### Lisää (projektin) taski tiimille
```curl --location --request POST 'http://localhost:8080/teams/791031a6-922b-4ea0-93da-ae7b21a7a09b/tasks/by-project-id/6e46e573-1bf4-46e9-a633-fb7447e42c16' --data ''```

### Assignoi taski tiimin jäsenelle
```curl --location --request PATCH 'http://localhost:8080/teams/791031a6-922b-4ea0-93da-ae7b21a7a09b/tasks/693ea04e-1baf-4d27-bf90-6ba6eb74aa31/assignee' --header 'Content-Type: application/json' --data '{"assigneeId":"c41c9a87-688f-428d-a1d5-4134f1faeeaf"}'```

### Ota taski käsittelyyn
```curl --location --request POST 'http://localhost:8080/teams/6c63a769-d65e-4e5b-899f-8cf8094e3cc8/tasks/693ea04e-1baf-4d27-bf90-6ba6eb74aa31/mark-in-progress'```

### Merkitse taski valmiiksi
```curl --location 'curl --location 'http://localhost:8080/teams/6c63a769-d65e-4e5b-899f-8cf8094e3cc8/tasks/693ea04e-1baf-4d27-bf90-6ba6eb74aa31/complete' --header 'Content-Type: application/json' --data '{"hours":8,"minutes":33}'```

### Hyväksy projekti
```curl --location 'http://localhost:8080/projects/c9db054a-b209-4450-a5a9-3c66bf9be4a3/approvals/a1d87f5b-dc0f-41bb-a645-331668a3e7ba' --header 'Content-Type: application/json' --data '{"approved":true,"reason":""}'```

### Hylkää projekti
```curl --location 'http://localhost:8080/projects/c9db054a-b209-4450-a5a9-3c66bf9be4a3/approvals/91712f21-f9e3-4f57-a3dc-c84d75b1d051' --header 'Content-Type: application/json' --data '{"approved":false,"reason":"not enough tests"}''```

### Unassignoi task
```curl --location --request POST 'http://localhost:8080/teams/6c63a769-d65e-4e5b-899f-8cf8094e3cc8/tasks/10b6d2b1-6c7e-4dda-9ccd-c71d9452528a/unassign' --data ''```
### Poista annettu task tiimiltä
```curl --location --request DELETE 'http://localhost:8080/teams/6c63a769-d65e-4e5b-899f-8cf8094e3cc8/tasks/10b6d2b1-6c7e-4dda-9ccd-c71d9452528a'```

### Hae annettu projekti
```curl --location 'http://localhost:8080/projects/c9db054a-b209-4450-a5a9-3c66bf9be4a3' --data ''```

### Hae annettu tiimi
```curl --location 'http://localhost:8080/teams/6c63a769-d65e-4e5b-899f-8cf8094e3cc8'```

### Hae hyväksynnät projektille
```curl --location 'http://localhost:8080/projects/c9db054a-b209-4450-a5a9-3c66bf9be4a3/approvals'```

## Rajoitteet ja huomiot

- Tämä projekti demonstroi lähinnä DDD ja Event sourcing (Axon) ohjelmointimallin osaamista. Siinä ei ole toteutettu mm. oikeaa autentikoitumista tai minkäänlaista käyttöliittymää.
- Yksikkötestit on tehty vain demonstroimaan AggregateTestFixture:n käyttöä
- Huom: Axon-malli sisältää projektin hyväksymisprosessin (saga), joka vaatii useita hyväksyjiä. Spring Data JDBC- ja reaktiivinen malli käyttävät yksinkertaisempaa "yhteyshenkilö" -mallia, sillä niiden tarkoituksena on demonstroida eri datamallien toimivuutta ilman hyväksyntälogiikan kompleksisuutt
- Mallinnuksessa Approval entiteetillä on oma UUID, vaikka käytännössä projectId + approverEmail voisi toimia luonnollisena avaimena. Tämä ratkaisu valittiin yksinkertaisuuden ja testattavuuden vuoksi. Lisäksi koko approval käsite on hiukan häilyvä, koska saman toiminnallisuuden olisi voinut rakentaa project aggregaatin avulla, mutta tässä haluttiin demonstroida saga patternia
- Sovelluksessa on pieniä puutteita, esimerkiksi Objects.requireNonNull voisi olla hyvä lisä esim command-recordien kentille

## Kehittäjä

- Toteuttanut Tapio Niemelä. Portfolio toimii todisteena osaamisesta:
- Domain Driven Design (aggregaatit, säännöt, eventit)
- Axon ja event sourcing
- Java + Spring Boot + Spring JPA
- Käytännöllinen REST-rajapinta
