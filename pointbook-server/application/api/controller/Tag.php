<?php
/**
 * Created by PhpStorm.
 * User: xxw
 * Date: 2018/5/7
 * Time: 17:46
 */

namespace app\api\controller;


use think\Controller;
use think\Db;
use think\Exception;
use think\Log;
use think\Request;

class Tag extends Controller
{
    public function get()
    {
        return null;
    }

    public function add()
    {
        $requestIns = Request::instance();
        $markerId = $requestIns->param('mid');
        $content = $requestIns->param('content');
        $userId = $requestIns->param('uid');

        try {
            $tag = new \app\api\model\Tag([
                'mid' => $markerId,
                'content' => $content,
                'uid' => $userId,
            ]);
            $tag->save();
        } catch (Exception $e) {
            Log::error($e->getMessage());
            return json("fail", 500);
        }
        return json($tag->id, 200);
    }

    public function delete()
    {
        $requestIns = Request::instance();
        $tagId = $requestIns->param('tid');

        try {
            $result = Db::table('tbl_marker_tag')->where('id', $tagId)->delete();
            return json($result, 200);
        } catch (Exception $e) {
            Log::error($e->getMessage());
            return json("fail", 500);
        }

    }
}