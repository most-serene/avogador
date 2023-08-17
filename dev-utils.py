from pygit2 import Repository
from cmd import Cmd
import os
from aeosa import appscript
from termcolor import colored

class DevUtilsCmd(Cmd):
    #{os.environ['USER']}-
    prompt = colored(f"{Repository('.').head.shorthand} > ", "light_green")
    intro = colored("Welcome to devUtil tool " + os.environ['USER'], "cyan")

    def do_localDevelopMode(self, inp):
        branch = Repository('.').head.shorthand
        currDir = os.getcwd()
        appscript.app('Terminal').do_script('cd ' + currDir + f'; BRANCH={branch} docker compose up --build')
        # os.system('docker compose up --build -d')

    def do_stopLocalDevelopMode(self, inp):
        os.system('BRANCH={branch} docker compose stop')
    
    def do_downLocalDevelopMode(self, inp):
        os.system('BRANCH={branch} docker compose down')

    def do_executeTests(self, inp):
        os.system('cd backend/apigateway; gradle test')
        os.system('cd backend/services/userservice; gradle test')
        os.system('cd backend/services/courseservice; gradle test')

    def do_exit(self, inp):
        return True
    
    def do_start(self, inp: str):
        branch = Repository('.').head.shorthand
        if inp == '':
            return
        for s in inp.split(' '):
            os.system(f'BRANCH={branch} docker compose start {s}')
    
    def do_restart(self, inp: str):
        branch = Repository('.').head.shorthand
        if inp == '':
            return
        for s in inp.split(' '):
            os.system(f'BRANCH={branch} docker compose build {s} && BRANCH={branch} docker compose create {s} && BRANCH={branch} docker compose restart {s}')

    def do_stop(self, inp: str):
        branch = Repository('.').head.shorthand
        if inp == '':
            return
        for s in inp.split(' '):
            os.system(f'BRANCH={branch} docker compose stop {s}')

    def do_list_services(self, inp):
        branch = Repository('.').head.shorthand
        os.system(f'BRANCH={branch} docker compose config --services')

    #### ----- ####

    def help_localDevelopMode(self):
        print('build and start the local backend cluster instance')
    
    def help_list_services(self):
        print('get a list of the services')

    def help_restart(self):
        print('rebuild, recreate and restart the listed services')

    def help_exit(self):
        print("exit the app")

    def help_stopLocalDevelopMode(self):
        print('stop the local backend cluster instance')

    def help_downLocalDevelopMode(self):
        print('remove the local backend cluster instance')

    def help_executeTests(self):
        print('execute all junit backend tests')

    def help_start(self):
        print('start the listed services')

    def help_stop(self):
        print('stop the listed services')


p = DevUtilsCmd()
p.cmdloop()
