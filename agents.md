Project use Java 8. No use of NodeJS or Python.



### Codex proxy servire for Maven

Maven should use Codex proxy server in order to access central repository:

   <mirrors>
     <mirror>
       <id>central-mirror</id>
       <mirrorOf>central</mirrorOf>
       <url>https://repo1.maven.org/maven2/</url>
     </mirror>
   </mirrors>
   <proxies>
     <proxy>
       <id>proxy</id>
       <active>true</active>
       <protocol>http</protocol>
       <host>proxy</host>
       <port>8080</port>
     </proxy>
   </proxies>



### GIT commits format

The first line in commit is short description of the commit.

Then should be empty line.

Then should be full description what and why was done. Put notes about the task that you solved and how it was soved. Put any notes that you have in mind.
