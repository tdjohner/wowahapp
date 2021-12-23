#Teagan Johner 2021 07 04

import mysql.connector
import requests
import time

b_client = '35bde0e980434302b4809be8713efef9'
b_secret = 'Q8vgPB7zwyxb1NTgez3eE3cqZbLIUOUI'

bad_profs = [794, 2777, 2787, 2791, 2811]
bad_tiers = [(182, 2551), (182,2552), (182,2553), (182,2554), (182,2555), (182,2556), (182,2760), (186,2565), (186,2566), (186,2567), (186,2761), (356,2585), (356,2586), (356,2587), (356,2588), (356,2589), (356,2590), (356,2591), (356,2592), (356,2754), (393,2558), (393,2559), (393,2560), (393,2561), (393,2562), (393,2563), (393,2564), (393,2557), (393,2762)]

wowah_db = mysql.connector.connect(
    host="localhost",
    user = "root",
    password= "bridge4four"
)

# returns access token
def create_access_token(client_id, client_secret, region = 'us'):
    data = { 'grant_type': 'client_credentials' }
    res = requests.post('https://%s.battle.net/oauth/token' % region, data=data, auth=(client_id, client_secret))
    return res.json()

# returns a list of profession IDs as json objects
def get_prof_index(token):
    res = requests.get('https://us.api.blizzard.com/data/wow/profession/index?namespace=static-us&locale=en_US&access_token=%s' % token)
    return res.json()['professions']


def get_prof_tiers(prof_id, token):
    url = 'https://us.api.blizzard.com/data/wow/profession/%s?namespace=static-us&locale=en_US&access_token=%s' % (prof_id, token)
    res = requests.get(url)
    print(url)
    return res.json()['skill_tiers']

def get_recipe_tiers(prof_id, tier_id, token):
    url = 'https://us.api.blizzard.com/data/wow/profession/%s/skill-tier/%s?namespace=static-us&locale=en_US&access_token=%s' % (prof_id, tier_id, token)
    res = requests.get(url)
    print(url)
    cursor = wowah_db.cursor()
    cursor.execute('USE wowahapp;')
    for r in res.json()['categories']:
        for c in r['recipes']:
            print("NAME", c['name'],"ID", c['id'], "TIER ID: ", tier_id, "PROF ID", prof_id)
            insert_command = "UPDATE tbl_recipes SET tierID = %s WHERE id = %s;" % (tier_id, c['id'])
            cursor.execute(insert_command)
            wowah_db.commit()




response = create_access_token(b_client, b_secret)
b_token = response['access_token']


prof_index = get_prof_index(b_token)

for prof_id in prof_index:
    if prof_id['id'] not in bad_profs: # filter out irrelevant professions' data
        tiers = get_prof_tiers(prof_id['id'], b_token)
        for t in tiers:
            time.sleep(2) #got a temp ban for too much requests
            #probably want to add t.name, t.id. prof.id as lookup table
            args_tuple = (prof_id['id'], t['id'])
            if (args_tuple not in bad_tiers):
                get_recipe_tiers(prof_id['id'], t['id'], b_token )




