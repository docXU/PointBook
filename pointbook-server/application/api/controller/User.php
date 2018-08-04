<?php
/**
 * Created by PhpStorm.
 * User: xxw
 * Date: 2018/5/9
 * Time: 11:04
 */

namespace app\api\controller;


use think\Controller;
use think\Exception;
use think\Log;
use think\Request;

class User extends Controller
{
    public function register()
    {
        $requestIns = Request::instance();
        $email = $requestIns->param('email');
        $password = $requestIns->param('password');

        try {
            $Registered = (new \app\api\model\User())
                ->where('email', $email)
                ->select();

            if (!is_array($Registered) || count($Registered,COUNT_NORMAL) == 1){
                return json([], 500);
            }

            $newuser = new \app\api\model\User([
                'email' => $email,
                'password' => $password,
                'headimg' => 'no',
                'username' => 'T'.time().rand(0,10),
                'sex' => 0
            ]);
            $newuser->save();

            $user = (new \app\api\model\User())
                ->where('email', $email)
                ->select();
            Log::info($user);

            $user[0]->password = null;
            return json(($user[0]), 200);

        } catch (Exception $e) {
            Log::error($e->getMessage());
            return json("fail", 500);
        }
    }

    public function get()
    {
        $requestIns = Request::instance();
        $id = $requestIns->param('uid');
        try {
            $user = (new \app\api\model\User())
                ->where('id', $id)
                ->select();
            Log::info($user);
            if (!is_array($user) || count($user,COUNT_NORMAL) == 0){
                return json([], 500);
            }else{
                $user[0]->password = null;
                return json(($user[0]), 200);
            }
        } catch (Exception $e) {
            Log::error($e->getMessage());
            return json("fail", 500);
        }
    }

    public function verify()
    {
        $requestIns = Request::instance();
        $email = $requestIns->param('email');
        $password = $requestIns->param('password');

        try {
            $user = (new \app\api\model\User())
                ->where('email', $email)
                ->where('password', $password)
                ->select();
            Log::info($user);
            if (!is_array($user) || count($user,COUNT_NORMAL) == 0){
                return json([], 200);
            }else{
                $user[0]->password = null;
                return json(($user[0]), 200);
            }
        } catch (Exception $e) {
            Log::error($e->getMessage());
            return json("fail", 500);
        }
    }

    public function update()
    {
        $requestIns = Request::instance();
        $id = $requestIns->param('id');
        $telephone = $requestIns->param('telephone');
        $wechat_id = $requestIns->param('wechat_id');
        $weibo_name = $requestIns->param('weibo_name');
        $sex = $requestIns->param('sex');
        $age = $requestIns->param('age');
        $describe = $requestIns->param('describe');
        $headimg = $requestIns->param('headimg');
        $username = $requestIns->param('username');

        try {
            $result = (new \app\api\model\User())->save([
                'telephone' => $telephone,
                'wechat_id' => $wechat_id,
                'weibo_name' => $weibo_name,
                'sex' => $sex,
                'age' => $age,
                'describe' => $describe,
                'headimg' => $headimg,
                'username' => $username
            ],['id' => $id]);
            if (!$result){
                return json([], 200);
            }else{
                $user = (new \app\api\model\User())
                    ->where('id', $id)
                    ->select();
                return json(($user[0]), 200);
            }
        } catch (Exception $e) {
            Log::error($e->getMessage());
            return json("fail", 500);
        }
    }
}