# Teams

A simple plugin that can group players together in numbered teams.
<br/>The teams are meant to be a simple cosmetic change (doesn't change any mechanics).

### Supported Versions: 1.16.5 - 1.20.1

## Player Commands

<pre>
/teams create [colour]
/teams disband
/teams invite [...players]
/teams join [number]
/teams kick [...players]
/teams leave
/teamsadmin 
</pre>

## Admin Commands

`requires teams.admin`
<pre>
/teamsadmin reload
</pre>

## Permissions

<pre>
teams.create: Create a team (default: true)
teams.disband: Disband a team (default: true)
teams.invite: Invite a player to the team (default: true)
teams.join: Join a team (default: true)
teams.kick: Kick a player out of the team (default: true)
teams.leave: Leave the team (default: true)
teams.admin: Allows use of plugin admin commands, such as teamsreload (default: op)
</pre>
