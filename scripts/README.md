Kaflib: makina berri batean ezarri eta deploy (release) egiteko. Hau
da, Kafliben bertsio berriak Mavenen publikatzeko.

Hemen, jarraitu ditudan pausoak zerrendatuko ditut. Ez dakit den-denak
beharrezkoak diren:

1) sudo apt remove gnupg
2) sudo apt install --reinstall gnupg2
3) sudo apt install dirmngr
4) sudo rm -rf ~/.gnupg/
5) gpg2 --gen-key
6) gpg2 --list-keys (key-id, adibidez, 5C45C08B dela ikusten dugu)
7) gpg2 --keyserver hkp://pgp.mit.edu --send-keys 5C45C08B
8) ~/.m2/settings.xml fitxategian, hurrengo edukia:

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>ossrh</id>
      <username>sgpbelez</username>
      <password>***</password> <!-- Benetazko pasahitza jarri -->
    </server>
  </servers>
  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg2</gpg.executable>
	<gpg.keyname>5C45C08B</gpg.keyname>
	<gpg.passphrase>***</gpg.passphrase> <!-- Benetazko passphrasea jarri, gpg2 gakoa sortzean erabilitakoa -->
	<gpg.defaultKeyring>false</gpg.defaultKeyring>
	<gpg.useagent>true</gpg.useagent>
	<gpg.lockMode>never</gpg.lockMode>
	<gpg.homedir>/home/zuhaitz/.gnupg</gpg.homedir>
      </properties>
    </profile>
  </profiles>
</settings>

9) release.sh scripta exekutatu, edo, "mvn clean deploy -P release"