# all the imports
import os
import sqlite3
from flask import Flask, request, session, g, redirect, url_for, abort, \
	 render_template, flash, jsonify, send_file
from werkzeug.utils import secure_filename
from subprocess import check_call
from shutil import copyfile
import tensorflow as tf


def gZipFile(fullFilePath):
	check_call(['tar', '-zcvf', 'uploads/data.tar.gz', fullFilePath])

UPLOAD_FOLDER = 'uploads'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])


app = Flask(__name__, static_folder="/public") # create the application instance :)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config.from_object(__name__) # load config from this file , flaskr.py

# Load default config and override config from an environment variable
app.config.update(dict(
	DATABASE=os.path.join(app.root_path, 'tfd_server.db'),
	SECRET_KEY=os.environ['TFD_PASS'],
	USERNAME='erikiado',
	PASSWORD=os.environ['TFD_PASS']
))
app.config.from_envvar('FLASKR_SETTINGS', silent=True)


def connect_db():
	"""Connects to the specific database."""
	rv = sqlite3.connect(app.config['DATABASE'])
	rv.row_factory = sqlite3.Row
	return rv

def get_db():
	"""Opens a new database connection if there is none yet for the
	current application context.
	"""
	if not hasattr(g, 'sqlite_db'):
		g.sqlite_db = connect_db()
	return g.sqlite_db

@app.teardown_appcontext
def close_db(error):
	"""Closes the database again at the end of the request."""
	if hasattr(g, 'sqlite_db'):
		g.sqlite_db.close()


def init_db():
	db = get_db()
	with app.open_resource('schema.sql', mode='r') as f:
		db.cursor().executescript(f.read())
	db.commit()

@app.cli.command('initdb')
def initdb_command():
	"""Initializes the database."""
	init_db()
	print('Initialized the database.')


@app.route('/')
def show_entries():
	db = get_db()
	cur = db.execute('select class from entries order by id desc')
	entries = cur.fetchall()
	u = {}
	for e in entries:
		print(e)
		if e['class'] not in u.keys():
			u[e['class']] = 1
		u[e['class']] += 1
	# print(entries)
	return render_template('show_entries.html', entries=u)

@app.route('/class/<class_name>')
def show_entries_class(class_name):
	db = get_db()	
	cur = db.execute('select class, filename from entries where class = (?) order by id desc', [class_name])
	entries = cur.fetchall()
	return render_template('show_entries_class.html', entries=entries)


@app.route("/class/<class_name>/dl")
def get_class_data(class_name):
	db = get_db()	
	dl_dir = UPLOAD_FOLDER + '/' + class_name
	copyfile('uploads/train.record',dl_dir+'/'+'train.record')
	cur = db.execute('select class, filename, coord1, coord2, coord3, coord4 from entries where class = (?) order by id desc', [class_name])
	gZipFile(dl_dir + '/')
	entries = cur.fetchall()
	for e in entries:
		print(e['class'],e['filename'],e['coord1'])
	# return render_template('show_entries_class.html', entries=entries)
	return send_file('../uploads/data.tar.gz', as_attachment=True)


@app.route('/login', methods=['GET', 'POST'])
def login():
	error = None
	if request.method == 'POST':
		if request.form['username'] != app.config['USERNAME']:
			error = 'Invalid username'
		elif request.form['password'] != app.config['PASSWORD']:
			error = 'Invalid password'
		else:
			session['logged_in'] = True
			flash('You were logged in')
			return redirect(url_for('show_entries'))
	return render_template('login.html', error=error)

@app.route('/logout')
def logout():
	session.pop('logged_in', None)
	flash('You were logged out')
	return redirect(url_for('show_entries'))


@app.route('/test/get', methods=['GET'])
def get_test():
	d = {
		'response': 'GET test working'
	}
	return jsonify(d)


def allowed_file(filename):
	return '.' in filename and \
		   filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/test/post', methods=['POST'])
def post_test():
	class_name = request.form['clase']
	row = request.form['csv_row'].split(',')
	image = request.files['pic']
	d = {}
	if image.filename == '':
		d['message'] =  'No selected file'
		return jsonify(d)    
	if image and allowed_file(image.filename):
		filename = secure_filename(image.filename)
		class_dir = os.path.abspath(UPLOAD_FOLDER) + '/' + class_name + '/'
		absolute_path = class_dir + filename
		coord1 = row[2]
		coord2 = row[3]
		coord3 = row[4]
		coord4 = row[5]
		if not os.path.exists(class_dir):
			os.makedirs(class_dir)
		image.save(absolute_path)
		db = get_db()
		db.execute('insert into entries (class, filename, coord1, coord2, coord3, coord4) values (?, ?, ?, ?, ?, ?)', [class_name, filename, coord1, coord2, coord3, coord4])
		db.commit()
		d['message'] =  filename + ' uploaded correctly.'
		return jsonify(d)
	d['message'] =  'File not allowed'
	return jsonify(d)

