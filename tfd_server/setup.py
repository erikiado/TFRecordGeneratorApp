from setuptools import setup

setup(
    name='tfd_server',
    packages=['tfd_server'],
    include_package_data=True,
    install_requires=[
        'flask',
    ],
)